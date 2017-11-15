package me.shreyasr.networking.network

sealed trait Packet {
  protected def write(stream: SerializationStream): Unit
}

object Packet {
  def readPacket(stream: SerializationStream): Packet = {
    stream.readByte() match {
      case 0 => new PingPacket(stream)
    }
  }

  def writePacket(stream: SerializationStream, packet: Packet): Unit = {
    packet match {
      case _: PingPacket => stream.writeByte(0);
    }
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

  override def toString = s"PingPacket ${counter}"
}
