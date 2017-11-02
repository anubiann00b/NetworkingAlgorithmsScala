package me.shreyasr.networking.system

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import me.shreyasr.networking._
import me.shreyasr.networking.component._

class InputProcessingSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family.all(
    classOf[IdComponent],
    classOf[InputComponent],
    classOf[PosComponent],
    classOf[VelComponent],
    classOf[DirComponent],
    classOf[ShipStatsComponent]).get, p) {

  override def processEntity(entity: Entity, delta: Float) = {
    val id = entity.get[IdComponent]
    val input = entity.get[InputComponent]
    val dir = entity.get[DirComponent]
    val pos = entity.get[PosComponent]
    val vel = entity.get[VelComponent]
    val stats = entity.get[ShipStatsComponent]

    if (input.thrust) {
      vel.dx += stats.thrust * Utils.cos(dir.dir)
      vel.dy += stats.thrust * Utils.sin(dir.dir)
    } else if (input.reverseThrust) {
      vel.dx += -stats.thrust * Utils.cos(dir.dir)
      vel.dy += -stats.thrust * Utils.sin(dir.dir)
    }

    if (input.turnCw) dir.dir -= stats.turn;
    if (input.turnCcw) dir.dir += stats.turn;

    if (input.fireLaser) {
      game.engine.addEntity(
        new Entity()
          .add(new ProjectileComponent(id.id))
          .add(new PosComponent(pos.x, pos.y))
          .add(new VelComponent(5*Utils.cos(dir.dir), 5*Utils.sin(dir.dir)))
          .add(new DirComponent(dir.dir))
          .add(new DrawingComponent(List(
                                      PolarLine(PolarPoint(0, 10), PolarPoint(180, 10))
                                    ))))
    }
  }
}
