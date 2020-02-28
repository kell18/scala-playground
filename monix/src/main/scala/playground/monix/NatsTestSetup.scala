package playground.monix

import io.nats.streaming.{Message, MessageHandler, StreamingConnection, StreamingConnectionFactory, Subscription, SubscriptionOptions}
import monix.reactive.Consumer
import scala.concurrent.duration._

object NatsTestSetup {
  val natsAckTmOut = 1.seconds

  var natsOptions = new SubscriptionOptions.Builder()
    .maxInFlight(5)
    .manualAcks()
    .durableName("local-tests1")
    .deliverAllAvailable()
    .ackWait(java.time.Duration.ofNanos(natsAckTmOut.toNanos))

  val natsConnectionConf = NatsConnectionConfig(
    "HIDDEN",
    "test-cluster",
    "reactive-sync_dev-albert",
    Some(natsAckTmOut)
  )
  val natsConnection = createConnection(natsConnectionConf)

  def subscribeToNats(msgHandler: MessageHandler): Subscription =
    natsConnection.subscribe("HIDDEN", msgHandler, natsOptions.build())

  final case class NatsConnectionConfig(
    uri: String,
    clusterId: String,
    clientId: String,
    pubAckTimeout: Option[Duration] = None
  )

  def createConnection(cfg: NatsConnectionConfig): StreamingConnection = {
    val connectionFactory = new StreamingConnectionFactory(cfg.clusterId, cfg.clientId)
    connectionFactory.setNatsUrl(cfg.uri)
    cfg.pubAckTimeout.foreach(d => connectionFactory.setAckTimeout(java.time.Duration.ofMillis(d.toMillis)))
    connectionFactory.createConnection()
  }


  val deduplicingConsumer = Consumer.foldLeft[Set[Long], Message](Set.empty) {
    case (seenMessages, msg) =>
      println(s"Consumer: consume ${msg.getSequence}")
      if (seenMessages.contains(msg.getSequence)) println(s"Consumer: got duplicate: ${msg.getSequence}")
      seenMessages + msg.getSequence
  }
}
