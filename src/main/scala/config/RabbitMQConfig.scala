package config

import com.rabbitmq.client.{Connection, ConnectionFactory}
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success, Try}

object RabbitMQConfig {
  private val config = ConfigFactory.load().getConfig("rabbitmq")

  private lazy val connectionFactory: ConnectionFactory = {
    val factory = new ConnectionFactory()
    factory.setHost(config.getString("host"))
    factory.setPort(config.getInt("port"))
    factory.setUsername(config.getString("username"))
    factory.setPassword(config.getString("password"))
    factory
  }

  def getConnection: Option[Connection] = {
    val connectionTry = Try(connectionFactory.newConnection())
    connectionTry match {
      case Success(connection) => Some(connection)
      case Failure(exception) =>
        println(s"Failed to create a connection: ${exception.getMessage}")
        None
    }
  }

  def closeConnection(connection: Connection): Unit = {
    Try(connection.close()) match{
      case Success(_) => println("Connection closed successfully!")
      case Failure(exception) => println(s"Error closing connection ${exception.getMessage}")
    }
  }
}
