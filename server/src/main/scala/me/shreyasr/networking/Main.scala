package me.shreyasr.networking

import java.net.SocketTimeoutException
import me.shreyasr.networking.network.{ SerializationStream, Socket }

object Main extends App {
  testSerialization()
  System.exit(0)

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
    val array = new Array[Byte](256)
    val writeStream = new SerializationStream(array)
    writeStream.writeInt(751294897);
    writeStream.writeInt(-751294897);
    writeStream.writeFloat(Math.PI.toFloat);
    writeStream.writeFloat(-Math.PI.toFloat);
    writeStream.writeLong(11112948753L);
    writeStream.writeLong(1112948753L);
    writeStream.writeLong(-18498752948753L);

    val readStream = new SerializationStream(array, writeStream.getPos());
    println(readStream.readInt())
    println(readStream.readInt())
    println(readStream.readFloat())
    println(readStream.readFloat())
    println(readStream.readLong())
    println(readStream.readLong())
    println(readStream.readLong())
  }
}
