package controllers

import java.util.UUID.randomUUID

import play.api.mvc.Request

object UserId {

  val key = "uuid"

  def apply[A](request: Request[A]): String =
    request.session.get(key) getOrElse randomUUID().toString
}
