package com.jensraaby.queuevis.sqs

import akka.actor.Actor
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.model.{ReceiveMessageResult, ReceiveMessageRequest}

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}

case class Poll(maxMessages: Int, maxWaitSeconds: Int)

case class Write(body: String)

class SqsQueue(queueUrl: String)(implicit ec: ExecutionContext) extends Actor {
  private def sqsClient = new AmazonSQSAsyncClient(new BasicAWSCredentials("x", "x"))

  def receive = {
    case Write(body) => sqsClient.sendMessage(queueUrl, body)
    case Poll(msgs, wait) =>
      val request = new ReceiveMessageRequest()
        .withQueueUrl(queueUrl)
        .withMaxNumberOfMessages(msgs)
        .withWaitTimeSeconds(wait)

      val (futureResult, handler) = futureHandler
      sqsClient.receiveMessageAsync(request, handler)
      futureResult foreach { r => sender() ! r }
  }

  private def futureHandler = {
    val p = Promise[ReceiveMessageResult]()
    val handler = new AsyncHandler[ReceiveMessageRequest, ReceiveMessageResult] {
      override def onSuccess(request: ReceiveMessageRequest, result: ReceiveMessageResult): Unit = p.complete(Success(result))

      override def onError(e: Exception): Unit = p.complete(Failure(e))
    }
    (p.future, handler)
  }

}
