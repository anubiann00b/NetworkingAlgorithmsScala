package me.shreyasr.networking.network

sealed abstract class Packet {
  def read(bytes: Array[Byte], len: Int): Unit
  def write(bytes: Array[Byte]): Int
}

object Packet {
  def readPacket(bytes: Array[Byte], len: Int): Packet = {
    bytes(0) match {
      case 0 => new PingPacket()
    }
  }
}

final class PingPacket extends Packet {
  def read(bytes: Array[Byte], len: Int): Unit = {

  }

  def write(bytes: Array[Byte]): Int = {
    bytes(0) == 0
    1
  }
}
