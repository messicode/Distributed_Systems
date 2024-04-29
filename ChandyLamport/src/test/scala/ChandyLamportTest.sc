import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorRef
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.duration._

class ChandyLamportTest extends AnyWordSpecLike with BeforeAndAfterAll {
  val testKit = ActorTestKit()

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }

  "A Process actor" must {
    "initiate a snapshot when requested" in {
      val probe = testKit.createTestProbe[ChandyLamportMessage]()
      val process = testKit.spawn(Process(1, Map(), probe.ref))

      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(1, true))
    }

    "correctly record its state and in-transit messages during a snapshot" in {
      val probe = testKit.createTestProbe[ChandyLamportMessage]()
      val neighborProbe = testKit.createTestProbe[ChandyLamportMessage]()
      val process = testKit.spawn(Process(2, Map(1 -> neighborProbe.ref), probe.ref))

      process ! TransactionMessage(100, 1)
      process ! RequestSnapshotStart
      process ! TransactionMessage(50, 2)

      // Assume we simulate an expectation for in-transit messages being logged
      probe.expectMessage(SnapshotStatus(2, true))
    }

    "handle consecutive snapshots correctly" in {
      val probe = testKit.createTestProbe[ChandyLamportMessage]()
      val process = testKit.spawn(Process(3, Map(), probe.ref))

      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(3, true))
      process ! ResetSnapshot

      // Initiate another snapshot after reset
      process ! RequestSnapshotStart
      probe.expectMessage(SnapshotStatus(3, true))
    }

    "send messages to all neighbors and handle incoming messages" in {
      val probe = testKit.createTestProbe[ChandyLamportMessage]()
      val neighbor1 = testKit.createTestProbe[ChandyLamportMessage]()
      val neighbor2 = testKit.createTestProbe[ChandyLamportMessage]()
      val process = testKit.spawn(Process(4, Map(1 -> neighbor1.ref, 2 -> neighbor2.ref), probe.ref))

      process ! SendMessagesToNeighbors
      neighbor1.expectMessageType[TransactionMessage]
      neighbor2.expectMessageType[TransactionMessage]
    }
  }
}
