package com.star.bright.trader.strategies

import com.cybozu.labs.langdetect.DetectorFactory
import com.kennycason.kumo.bg.PixelBoundryBackground
import com.kennycason.kumo.font.scale.LinearFontScalar
import com.kennycason.kumo.font.{FontWeight, KumoFont}
import com.kennycason.kumo.nlp.FrequencyAnalyzer
import com.kennycason.kumo.{CollisionMode, PolarWordCloud}
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.functions.unix_timestamp
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.twitter._
import org.elasticsearch.spark.rdd.EsSpark
import org.json4s.JsonDSL._
import twitter4j.Status

import java.awt.Dimension
import java.io.File
import java.nio.charset.{Charset, CodingErrorAction}
import java.util.Properties
import scala.collection.mutable
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.Try

case class ElasticSearchResources(
  resource: String = "sparksender/tweets",
  nodes: String = "localhost",
  port: String = "9200"
)

object TwitterSentimentAnalysis {

  def getTweets(
    ssc: StreamingContext,
    filters: mutable.IndexedSeq[String],
    elasticSearchResource: ElasticSearchResources = ElasticSearchResources(),
    profileFilename: String,
    configFilename: String
  ): Unit = {
    DetectorFactory.loadProfile(profileFilename)
    val twitterConfig = ConfigFactory.parseFile(new File(configFilename))
    val consumerKey = twitterConfig.getString("Twitter.secret.consumerKey")
    val consumerSecret = twitterConfig.getString("Twitter.secret.consumerSecret")
    val accessToken = twitterConfig.getString("Twitter.secret.accessToken")
    val accessTokenSecret = twitterConfig.getString("Twitter.secret.accessTokenSecret")

    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    // grab tweets
    val tweets: ReceiverInputDStream[Status] = TwitterUtils.createStream(ssc, None, filters)
    def sentiment(tweets: String): String = {
      var mainSentiment = 0
      var longest = 0;
      val sentimentText = Array("Very Negative", "Negative", "Neutral", "Positive", "Very Positive")
      val props = new Properties();
      props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
      new StanfordCoreNLP(props)
        .process(tweets)
        .get(classOf[CoreAnnotations.SentencesAnnotation])
        .asScala
        .foreach { (sentence: CoreMap) =>
          val sentiment =
            RNNCoreAnnotations.getPredictedClass(sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree]));
          val partText = sentence.toString();
          if (partText.length() > longest) {
            mainSentiment = sentiment;
            longest = partText.length();
          }
        }
      sentimentText(mainSentiment)
    }
    val tweetMap = tweets.map { status =>
      val formatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
      val tweetMap = ("user" -> status.getUser.getScreenName) ~
        ("createdAt" -> formatter.format(status.getCreatedAt.getTime)) ~ {
          if (status.getGeoLocation != null)
            ("latitude" -> status.getGeoLocation.getLatitude) ~ ("longitude" -> status.getGeoLocation.getLongitude)
          else
            ("latitude" -> "") ~ ("Longitude" -> "")
        } ~
        ("text" -> status.getText)
      ("retweet" -> status.getRetweetCount) ~
        ("sentiment" -> detectSentiment(status.getText).toString) ~
        ("hashtags" -> status.getHashtagEntities.map(_.getText).toList)

    }
    tweetMap.print
    tweetMap.map(s => List(s"Tweet Extracted: $s")).print

    // Each batch is saved to Elasticsearch with StatusCreatedAt as the default time dimension
    tweetMap.foreachRDD(t =>
      EsSpark.saveToEs(t, elasticSearchResource.resource, mutable.Map("es.mapping.timestamp" -> "createdAt"))
    )
    ssc.start()
    ssc.awaitTermination()
  }

  def getElasticSearchData(
    spark: SparkSession,
    find: String,
    elasticSearchResource: ElasticSearchResources = ElasticSearchResources()
  ): DataFrame = {

    import spark.sqlContext.implicits._
    val elasticSearchDataDF: DataFrame =
      spark.sqlContext.read.format("org.elasticsearch.spark.sql").load(elasticSearchResource.resource)
    elasticSearchDataDF.createOrReplaceTempView("es_data")

    val company = spark.sqlContext.sql(s"""SELECT * FROM es_data where filter = "$find"""")
    company.createOrReplaceTempView("company")

    val ts = unix_timestamp($"created_at", "EEE MMM dd HH:mm:ss ZZZ yyyy").cast("timestamp")
    val timeGrasp = company.withColumn("timestamp", ts)
    timeGrasp.createOrReplaceTempView("time_grasp")

    val timeIntervalDF: DataFrame = spark.sqlContext.sql(
      s"""Select created_at, title, retweet_count, sentiment, from_unixtime(floor(unix_timestamp(timestamp)/3600)*3600) as timeslice from time_grasp where timestamp>=cast('2017-05-01' as date) and timestamp<cast('2017-05-11' as date)"""
    )

    timeIntervalDF
  }

  def detectLanguage(text: String): String =
    Try {
      val detector = DetectorFactory.create()
      detector.append(text)
      detector.detect()
    }.getOrElse("unknown")

  def plotSentiments(spark: SparkSession, time_interval: DataFrame, find: String): Unit = {
    time_interval.createOrReplaceTempView("time_interval")
    val path = System.getProperty("analysis.dir")
    import spark.sqlContext.implicits._
    val sentimentCountDF: DataFrame = spark.sqlContext.sql(
      "select sentiment, count(*) as sentimentcount, timeslice from time_interval group by sentiment, timeslice"
    )
    sentimentCountDF.map(row => row.mkString(",")).coalesce(1).write.text(path + find + "_sentiment.csv")
  }

  def plotWordCloud(spark: SparkSession, time_interval: DataFrame, find: String, stopFile: String): Unit = {
    time_interval.createOrReplaceTempView("time_interval")

    val getPosText = spark.sqlContext.sql(s"""select title from time_interval where sentiment="positive"""")

    val getNegText = spark.sqlContext.sql(s"""select title from time_interval where sentiment="negative"""")

    val getNeuText = spark.sqlContext.sql(s"""select title from time_interval where sentiment="neutral"""")

    val negative = getNegText.select("title").rdd.flatMap(r => r(0).asInstanceOf[String].split(" ")).collect().toList
    val positive = getPosText.select("title").rdd.flatMap(r => r(0).asInstanceOf[String].split(" ")).collect().toList
    val neutral = getNeuText.select("title").rdd.flatMap(r => r(0).asInstanceOf[String].split(" ")).collect().toList

    if (negative.take(1).length == 1 && positive.take(1).length == 1)
      return

    val decoder = Charset.forName("UTF-8").newDecoder()

    decoder.onMalformedInput(CodingErrorAction.IGNORE)

    val pos = positive.map(x => x.replaceAll("\\p{C}", "?"))
    val neg = negative.map(x => x.replaceAll("\\p{C}", "?"))
    val neu = neutral.map(x => x.replaceAll("\\p{C}", "?"))

    plotWhale(pos, neg, find, stopFile)
  }

  def plotWhale(pos: List[String], neg: List[String], find: String, stopfile: String): Unit = {
    val path = System.getProperty("analysis.dir")
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val listOfLines = Source.fromFile(stopfile)(decoder).getLines.toSet
    val frequencyAnalyzer = new FrequencyAnalyzer
    frequencyAnalyzer.setWordFrequenciesToReturn(600)
    frequencyAnalyzer.setMinWordLength(4)
    frequencyAnalyzer.setStopWords(listOfLines.asJava)

    val wordFrequencies = frequencyAnalyzer.load(pos.asJava)
    val wordFrequencies2 = frequencyAnalyzer.load(neg.asJava)

    val dimension = new Dimension(1600, 924)
    val wordCloud = new PolarWordCloud(dimension, CollisionMode.PIXEL_PERFECT)
    wordCloud.setPadding(2)

    wordCloud.setBackgroundColor(java.awt.Color.WHITE)
    wordCloud.setKumoFont(new KumoFont("Impact", FontWeight.PLAIN))
    wordCloud.setBackground(new PixelBoundryBackground(path + "background.jpg"))
    wordCloud.setFontScalar(new LinearFontScalar(30, 80))
    wordCloud.build(wordFrequencies, wordFrequencies2)
    wordCloud.writeToFile(path + find + "_wordcloud.png")
  }

}
