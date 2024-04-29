import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorRef
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration._

class LaiYangTest extends AnyWordSpecLike with BeforeAndAfterAll {
  val testKit = ActorTestKit()

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }

  "A Process actor" must {
    "initiate a snapshot when requested" in {
      val probe = testKit.createTestProbe[Message]()
      val process = testKit.spawn(Process(1, Map(), probe.ref))

      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(1, true))
    }

    "correctly record its state and in-transit messages during a snapshot" in {
      val probe = testKit.createTestProbe[Message]()
      val neighborProbe = testKit.createTestProbe[Message]()
      val process = testKit.spawn(Process(2, Map(1 -> neighborProbe.ref), probe.ref))

      process ! BasicMessage(100, tag = false, from = neighborProbe.ref, messageId = 1)
      process ! RequestSnapshotStart
      process ! BasicMessage(50, tag = true, from = neighborProbe.ref, messageId = 2)

      neighborProbe.expectMessageType[AcknowledgeMessage]
      probe.expectMessage(SnapshotStatus(2, true))
      // Ensure the snapshot captures the in-transit message that was sent after snapshot initiation
      probe.expectMessageType[ControlMessage]
    }

    "handle consecutive snapshots correctly" in {
      val probe = testKit.createTestProbe[Message]()
      val process = testKit.spawn(Process(3, Map(), probe.ref))

      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(3, true))
      process ! CompleteSnapshot
      process ! ResetSnapshot

      // Initiate another snapshot after reset
      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(3, true))
    }
  }
}
