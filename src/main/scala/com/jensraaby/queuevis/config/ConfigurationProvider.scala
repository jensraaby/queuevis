package com.jensraaby.queuevis.config

import com.typesafe.config.ConfigFactory

trait ConfigurationProvider {
  def queueUrl: String
}

object ConfigurationProvider {
  implicit def defaultProvider = DefaultConfiguration
}

object DefaultConfiguration extends ConfigurationProvider {
  val conf = ConfigFactory.load()

  override lazy val queueUrl: String = conf.getString("sqs.queueUrl")
}
