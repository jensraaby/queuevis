package com.jensraaby.queuevis

import akka.actor.{ActorSystem, Props}
import com.amazonaws.services.sqs.model.ReceiveMessageResult
import com.jensraaby.queuevis.sqs.{Write, SqsQueue, Poll}
import org.elasticmq.rest.sqs.SQSRestServerBuilder

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class QueueVis {
  implicit val ec = ExecutionContext.global
  implicit val timeout: akka.util.Timeout = 20.seconds
  val system = ActorSystem("quevis-system")

  val elasticMQserver = SQSRestServerBuilder.withActorSystem(system).withPort(9325).withInterface("localhost").start()

  val queue = system.actorOf(Props(new SqsQueue("http://localhost:9325/queue1")))
}

object QueueVisMain extends QueueVis with App {

  import akka.pattern.ask

  val postSomething = queue ! Write("hello world")

  val results = (queue ? Poll(2, 10)).mapTo[ReceiveMessageResult]
  results.map { r =>
    println(r.getMessages)
  }

  Await.result(results, 5.minutes)
  elasticMQserver.stopAndWait()
}