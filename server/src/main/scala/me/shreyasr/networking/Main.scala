package me.shreyasr.networking

import me.shreyasr.networking.network.Socket

object Main extends App {
  println("Starting server")
  val socket = new Socket(Some(Socket.SERVER_PORT))
  socket.listen(packet => {
                  println(s"Received packet! ${packet}")
                })
}
