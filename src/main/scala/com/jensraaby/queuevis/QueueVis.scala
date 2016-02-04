package com.jensraaby.queuevis

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.model.{ReceiveMessageResult, Message, SendMessageResult}
import com.jensraaby.queuevis.aws.SqsQueue
import com.jensraaby.queuevis.config.ConfigurationProvider

import scala.concurrent.{Future, Await, ExecutionContext}
import scala.concurrent.duration._

class QueueVis(queueUrl: String)(implicit ec: ExecutionContext, timeout: Timeout) {
  implicit val sqsClient = new AmazonSQSAsyncClient()
  val system = ActorSystem("queueVis-system")
  val queue = system.actorOf(SqsQueue(queueUrl))

  override def finalize(): Unit = {
    println("Shutting down SQS client")
    sqsClient.shutdown()
  }

  def postMessage(messageBody: String): Future[String] = {
    (queue ? SqsQueue.Write(messageBody))
      .mapTo[SendMessageResult]
      .map(_.getMessageId)
  }

  def getMessages(maxMessages: Int, maxWait: FiniteDuration): Future[List[Message]] = {
    (queue ? SqsQueue.Poll(maxMessages, maxWait.toSeconds.toInt))
      .mapTo[List[Message]]
  }


}

object QueueVisMain extends App {
  implicit val timeout: Timeout = 20.seconds
  implicit val ec = ExecutionContext.global
  val config = implicitly[ConfigurationProvider]
  val queueVis = new QueueVis(config.queueUrl)

//  val postSomething = queueVis.postMessage("hello from the main app")
//  val messageId = Await.result(postSomething, timeout.duration)
//  println("message id: " + messageId)


  val messages = Await.result(queueVis.getMessages(10, 10.second), 20.seconds)
  println("got messages: " + messages)

  queueVis.finalize()
  queueVis.system.terminate()
  println("all done")
}