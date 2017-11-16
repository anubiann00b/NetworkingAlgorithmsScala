package me.shreyasr.networking.network

import com.badlogic.ashley.core.Entity
import me.shreyasr.networking.component._
import scala.collection.JavaConverters._

sealed trait Packet {
  protected def write(stream: SerializationStream): Unit
}

object Packet {
  def readPacket(stream: SerializationStream): Packet = {
    stream.readByte() match {
      case 0 => new PingPacket(stream)
      case 1 => new InputPacket(stream)
      case 2 => new GameStatePacket(stream)
    }
  }

  def writePacket(stream: SerializationStream, packet: Packet): Unit = {
    val packetCode: Byte = packet match {
      case _: PingPacket => 0
      case _: InputPacket => 1
      case _: GameStatePacket => 2
    }
    stream.writeByte(packetCode)
    packet.write(stream);
  }
}

final class PingPacket(val counter: Int) extends Packet {

  def this(stream: SerializationStream) = {
    this(stream.readInt())
  }

  override def write(stream: SerializationStream): Unit = {
    stream.writeInt(counter)
  }

  override def toString = s"PingPacket $counter"
}

final class InputPacket(val input: InputComponent) extends Packet {

  def this(stream: SerializationStream) = {
    this(new InputComponent(stream))
  }

  override def write(stream: SerializationStream): Unit = {
    input.write(stream)
  }

  override def toString = s"InputPacket $input"
}

final class GameStatePacket(val entities: List[Entity]) extends Packet {

  def this(stream: SerializationStream) = {
    this(GameStatePacket.getEntitiesFromStream(stream))
  }

  override def toString = s"GameStatePacket with ${entities.length} entities"

  override def write(stream: SerializationStream): Unit = {
    stream.writeInt(entities.length)
    entities.foreach(writeEntity(stream, _))
  }

  private def writeEntity(stream: SerializationStream, e: Entity) = {
    val components = e.getComponents
    stream.writeInt(components.size)
    components.asScala
      .map(_.asInstanceOf[Components])
      .foreach(writeComponent(stream, _))
  }

  private def writeComponent(stream: SerializationStream, c: Components) = {
    val id = c match {
      case _: IdComponent => 0
      case _: PosComponent => 1
      case _: VelComponent => 2
      case _: DirComponent => 3
      case _: ShipStatsComponent => 4
      case _: DrawingComponent => 5
      case _: ProjectileComponent => 6
      case _: CameraFocusComponent => 7
      case _: InputComponent => 8
    }
    stream.writeInt(id)
    c.write(stream)
  }
}

object GameStatePacket {

  def getEntitiesFromStream(stream: SerializationStream): List[Entity] = {
    val numEntities = stream.readInt()
    (1 to numEntities).map(_ => readEntity(stream)).toList
  }

  private def readEntity(stream: SerializationStream): Entity = {
    val numComponents = stream.readInt()
    val entity = new Entity()

    (1 to numComponents)
      .map(_ => readComponent(stream))
      .foreach(entity.add(_))

    entity
  }

  private def readComponent(stream: SerializationStream): Components = {
    val componentId = stream.readInt()
    componentId match {
      case 0 => new IdComponent(stream)
      case 1 => new PosComponent(stream)
      case 2 => new VelComponent(stream)
      case 3 => new DirComponent(stream)
      case 4 => new ShipStatsComponent(stream)
      case 5 => new DrawingComponent(stream)
      case 6 => new ProjectileComponent(stream)
      case 7 => new CameraFocusComponent()
      case 8 => new InputComponent(stream)
    }
  }
}
