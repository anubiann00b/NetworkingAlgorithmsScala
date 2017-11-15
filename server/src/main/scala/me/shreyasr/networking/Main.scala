package me.shreyasr.networking

import java.net.SocketTimeoutException
import me.shreyasr.networking.network.{ SerializationStream, Socket }

object Main extends App {
  println("Starting server")
  val socket = new Socket(Some(Socket.SERVER_PORT))

  try {
    socket.listen(
      packet => {
        println(s"Received packet! ${packet}")
      })
  } catch {
    case e: SocketTimeoutException => println("Socket timed out")
  } finally {
    socket.close();
  }

  def testSerialization(): Unit = {
    val array = new Array[Byte](4)
    val writeStream = new SerializationStream(array)
    writeStream.writeInt(751294897);

    val readStream = new SerializationStream(array, writeStream.getPos());
    println(readStream.readInt())
  }
}
