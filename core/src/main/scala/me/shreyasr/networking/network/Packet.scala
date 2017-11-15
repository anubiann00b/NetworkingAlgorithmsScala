package me.shreyasr.networking.network

import me.shreyasr.networking.component.InputComponent


sealed trait Packet {
  protected def write(stream: SerializationStream): Unit
}

object Packet {
  def readPacket(stream: SerializationStream): Packet = {
    stream.readByte() match {
      case 0 => new PingPacket(stream)
      case 1 => new InputPacket(stream)
    }
  }

  def writePacket(stream: SerializationStream, packet: Packet): Unit = {
    val packetCode: Byte = packet match {
      case _: PingPacket => 0;
      case _: InputPacket => 1;
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
