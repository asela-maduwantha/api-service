package controllers

import dto.Employee
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.{StatusCodes, HttpEntity, ContentTypes}
import org.apache.pekko.http.scaladsl.server.Route
import publisher.Publisher
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, Formats}

object EmployeeController {
  implicit val formats: Formats = DefaultFormats

  val route: Route =
    pathPrefix("employee") {
      concat(
        post {
          entity(as[String]) { jsonString =>
            val employee = read[Employee](jsonString)
            val requestPayload = s"create:${write(employee)}"
            Publisher.sendRequest(requestPayload) match {
              case Some(response) =>
                println(response)
                complete(HttpEntity(ContentTypes.`application/json`, response))
              case None =>
                complete(StatusCodes.InternalServerError, "Failed to process create employee request")
            }
          }
        },
        get {
          parameter("id") { id =>
            val requestPayload = s"get:$id"
            Publisher.sendRequest(requestPayload) match {
              case Some(response) =>
                println(response)
                complete(HttpEntity(ContentTypes.`application/json`, response))
              case None =>
                complete(StatusCodes.InternalServerError, s"Failed to process get employee request for id: $id")
            }
          }
        },
        put {
          entity(as[String]) { jsonString =>
            val employee = read[Employee](jsonString)
            val requestPayload = s"update:${write(employee)}"
            Publisher.sendRequest(requestPayload) match {
              case Some(response) =>
                println(response)
                complete(HttpEntity(ContentTypes.`application/json`, response))
              case None =>
                complete(StatusCodes.InternalServerError, "Failed to process update employee request")
            }
          }
        },
        delete {
          parameter("id") { id =>
            val requestPayload = s"delete:$id"
            Publisher.sendRequest(requestPayload) match {
              case Some(response) =>
                println(response)
                complete(HttpEntity(ContentTypes.`application/json`, response))
              case None =>
                complete(StatusCodes.InternalServerError, s"Failed to process delete employee request for id: $id")
            }
          }
        }
      )
    }
}
