package me.shreyasr.networking

import com.badlogic.gdx.backends.lwjgl._
import me.shreyasr.networking.network.{ PingPacket, Socket }

object Main extends App {
  val socket = new Socket()
  socket.connect("127.0.0.1")
  socket.send(new PingPacket)

  val config = new LwjglApplicationConfiguration
  config.title = "NetworkingAlgorithms"
  config.height = 1000
  config.width = 1280
  config.forceExit = false
  new LwjglApplication(new NetworkingAlgorithms(socket), config)
}
