package publisher

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{ DefaultConsumer, Envelope}
import com.typesafe.config.ConfigFactory
import config.RabbitMQConfig
import java.util.UUID
import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}

object Publisher {
  private val config = ConfigFactory.load()
  private val rabbitmqConfig = config.getConfig("rabbitmq")
  private val QUEUE_NAME = rabbitmqConfig.getString("queueName")

  private val connection = RabbitMQConfig.getConnection.get
  private val channel = connection.createChannel()

  def sendRequest(message: String): Option[String] = {

      val replyQueueName = channel.queueDeclare().getQueue
      val correlationId  = UUID.randomUUID().toString

      val props: BasicProperties = new BasicProperties.Builder()
        .correlationId(correlationId)
        .replyTo(replyQueueName)
        .build()

      channel.basicPublish("", QUEUE_NAME, props, message.getBytes("UTF-8"))

      val responseQueue = new ArrayBlockingQueue[String](1)

      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(
                                     consumerTag: String,
                                     envelope: Envelope,
                                     properties: BasicProperties,
                                     body: Array[Byte]
                                   ): Unit = {
          if (properties.getCorrelationId == correlationId) {
            responseQueue.offer(new String(body, "UTF-8"))
          }
        }
      }
      channel.basicConsume(replyQueueName, true, consumer)

      Option(responseQueue.poll(5, TimeUnit.SECONDS))
    }
}
