package me.shreyasr.networking.system

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import me.shreyasr.networking._
import me.shreyasr.networking.component._

class VelocityUpdateSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family.all(
    classOf[PosComponent],
    classOf[VelComponent]
  ).get, p) {

  override def processEntity(entity: Entity, delta: Float) = {
    val pos = entity.get[PosComponent]
    val vel = entity.get[VelComponent]
    val stats = entity.getOpt[ShipStatsComponent]

    pos.x += vel.dx
    pos.y += vel.dy

    stats.map(_.maxSpeed).foreach(
      maxSpeed => {
        if (vel.magnitude > maxSpeed) {
          val scaling = vel.magnitude / maxSpeed

          vel.dx /= scaling
          vel.dy /= scaling
        }
      })

  }
}
