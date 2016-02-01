package com.jensraaby.queuevis.sqs

import akka.actor.Actor
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.model.{ReceiveMessageResult, ReceiveMessageRequest}

case class Poll(maxMessages: Int, maxWaitSeconds: Int)
case class Write(body: String)

class SqsQueue(queueUrl: String) extends Actor {
  private def sqsClient = new AmazonSQSAsyncClient(new BasicAWSCredentials("x", "x"))

  def receive = {
    case Write(body) => sqsClient.sendMessage(queueUrl, body)
    case Poll(msgs, wait) =>
      val request = new ReceiveMessageRequest()
      .withQueueUrl(queueUrl)
      .withMaxNumberOfMessages(msgs)
      .withWaitTimeSeconds(wait)

      val result: ReceiveMessageResult = sqsClient.receiveMessage(request)
      sender ! result.getMessages
  }

}
