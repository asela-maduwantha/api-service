package controllers

import dto.Employee
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.{StatusCodes, HttpEntity, ContentTypes}
import org.apache.pekko.http.scaladsl.server.{Route, StandardRoute}
import publisher.Publisher
import org.json4s.jackson.Serialization.{read, write}
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.LoggerFactory

object EmployeeController {
  private val logger = LoggerFactory.getLogger(getClass)
  implicit val formats: Formats = DefaultFormats

  private def handleResponse(operation: String)(response: Option[String]): StandardRoute = {
    response match {
      case Some(result) =>
        logger.info(s"Successfully processed $operation request: $result")
        complete(HttpEntity(ContentTypes.`application/json`, result))
      case None =>
        logger.error(s"Failed to process $operation request")
        complete(StatusCodes.InternalServerError, s"Failed to process $operation request")
    }
  }

  private def processEmployeeOperation(operation: String, payload: String) = {
    Publisher.sendRequest(s"$operation:$payload") match {
      case Some(response) => handleResponse(operation)(Some(response))
      case None => handleResponse(operation)(None)
    }
  }

  val route: Route =
    pathPrefix("employee") {
      concat(
        post {
          entity(as[String]) { jsonString =>
            val employee = read[Employee](jsonString)
            processEmployeeOperation("create", write(employee))
          }
        },
        get {
          parameter("id") { id =>
            processEmployeeOperation("get", id)
          }
        },
        put {
          entity(as[String]) { jsonString =>
            val employee = read[Employee](jsonString)
            processEmployeeOperation("update", write(employee))
          }
        },
        delete {
          parameter("id") { id =>
            processEmployeeOperation("delete", id)
          }
        }
      )
    }
}