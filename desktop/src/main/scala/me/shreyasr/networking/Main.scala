package me.shreyasr.networking

import com.badlogic.gdx.backends.lwjgl._

object Main extends App {
  val config = new LwjglApplicationConfiguration
  config.title = "NetworkingAlgorithms"
  config.height = 1000
  config.width = 1280
  config.forceExit = false
  new LwjglApplication(new NetworkingAlgorithms, config)
}
