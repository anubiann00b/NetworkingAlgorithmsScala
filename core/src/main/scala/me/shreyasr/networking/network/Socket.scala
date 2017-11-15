package me.shreyasr.networking.network

import java.net.{ DatagramPacket, DatagramSocket, InetAddress }


class Socket(port: Option[Int] = None) {
  import Socket._

  val rawSocket = port match {
    case Some(port) => new DatagramSocket(port)
    case None => new DatagramSocket()
  }

  val writeBuffer = new Array[Byte](1024)
  val readBuffer = new Array[Byte](1024)

  def connect(ip: String) = {
    rawSocket.connect(InetAddress.getByName(ip), SERVER_PORT)
  }

  def close() = {
    rawSocket.close()
  }

  def send(packet: Packet) = {
    val stream = new SerializationStream(writeBuffer)
    Packet.writePacket(stream, packet)
    rawSocket.send(new DatagramPacket(writeBuffer, stream.getPos()))
  }

  def listen(listener: Packet => Unit) = {
    rawSocket.setSoTimeout(10000)
    val recvPacket = new DatagramPacket(readBuffer, readBuffer.length)

    while (true) {
      rawSocket.receive(recvPacket)
      listener(Packet.readPacket(
                 new SerializationStream(readBuffer, recvPacket.getLength)))
    }
  }
}

object Socket {
  val SERVER_PORT = 59999
}
