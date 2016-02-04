package com.jensraaby.queuevis.aws

import akka.actor.{Props, Actor}
import akka.pattern.pipe
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.model.{SendMessageRequest, SendMessageResult, ReceiveMessageResult, ReceiveMessageRequest}

import scala.concurrent.ExecutionContext

object SqsQueue {
  case class Poll(maxMessages: Int, maxWaitSeconds: Int)
  case class Write(body: String)


  def apply(queueUrl: String)(implicit executionContext: ExecutionContext,
                              sqsClient: AmazonSQSAsyncClient) = Props(new SqsQueue(queueUrl)(executionContext, sqsClient))
}

class SqsQueue(queueUrl: String)(implicit ec: ExecutionContext, sqsClient: AmazonSQSAsyncClient) extends Actor with AsyncWrapper {
  def receive = {
    case SqsQueue.Write(body) =>
      println("Writing message: " + body)
      val request = new SendMessageRequest()
        .withMessageBody(body)
        .withQueueUrl(queueUrl)

      val (result, handler) = asyncHandler[SendMessageRequest, SendMessageResult]
      sqsClient.sendMessageAsync(request, handler)
      println("Sent message")
      result pipeTo sender()

    case SqsQueue.Poll(msgs, wait) =>
      val destination = sender()
      val request = new ReceiveMessageRequest()
        .withQueueUrl(queueUrl)
        .withMaxNumberOfMessages(msgs)
        .withWaitTimeSeconds(wait)

      val (result, handler) = asyncHandler[ReceiveMessageRequest, ReceiveMessageResult]
      sqsClient.receiveMessageAsync(request, handler)
      import scala.collection.JavaConverters._
      for (results <- result) {
        destination ! results.getMessages.asScala.toList
      }
  }

}
