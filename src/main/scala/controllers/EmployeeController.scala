package controllers

import dto.Employee
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.{StatusCodes, HttpEntity, ContentTypes}
import org.apache.pekko.http.scaladsl.server.Route
import publisher.Publisher
import org.json4s.jackson.Serialization.read
import org.json4s.{DefaultFormats, Formats}

object EmployeeController {
  implicit val formats: Formats = DefaultFormats

  val route: Route =
    pathPrefix("employee") {
      post {
        entity(as[String]) { jsonString =>
          val employee = read[Employee](jsonString)
          Publisher.createEmployee(employee)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"Employee event published for ${employee.name}"))
        }
      }
    }
}
