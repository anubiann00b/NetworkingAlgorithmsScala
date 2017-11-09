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

  def send(packet: Packet) = {
    val len = packet.write(writeBuffer)
    rawSocket.send(new DatagramPacket(writeBuffer, len))
  }

  def listen(listener: Packet => Unit) = {
    val recvPacket = new DatagramPacket(readBuffer, readBuffer.length)

    while (true) {
      rawSocket.receive(recvPacket)
      listener(Packet.readPacket(readBuffer, recvPacket.getLength()))
    }
  }
}

object Socket {
  val SERVER_PORT = 59999
}
