package com.jensraaby.queuevis.aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait AsyncWrapper {
  def asyncHandler[RequestType <: AmazonWebServiceRequest, ResultType] = {
    val p = Promise[ResultType]()
    val handler = new AsyncHandler[RequestType, ResultType] {
      override def onSuccess(request: RequestType, result: ResultType): Unit =
        p.complete(Success(result))
      override def onError(e: Exception): Unit =
        p.complete(Failure(e))
    }
    (p.future, handler)
  }
}
