package me.shreyasr.networking.system

import com.badlogic.ashley.core.{ Engine, Entity, Family }
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{ Intersector, Vector2 }
import me.shreyasr.networking._
import me.shreyasr.networking.component._

import scala.collection.JavaConverters._
import scala.util.Try

class MissileLogicUpdateSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family
    .all(
      classOf[ProjectileComponent],
      classOf[PosComponent],
      classOf[DirComponent],
      classOf[VelComponent],
      classOf[ShipStatsComponent])
    .exclude(
      classOf[IdComponent]).get, p) {

  var ships: ImmutableArray[Entity] = null

  override def addedToEngine(engine: Engine) = {
    super.addedToEngine(engine)
    ships = getEngine.getEntitiesFor(
      Family
        .all(
          classOf[IdComponent],
          classOf[PosComponent]).get
    )
  }

  override def removedFromEngine(engine: Engine) = {
    super.removedFromEngine(engine)
    ships = null;
  }

  override def processEntity(entity: Entity, delta: Float) = {
    val projectile = entity.get[ProjectileComponent]
    val dir = entity.get[DirComponent]
    val pos = entity.get[PosComponent]
    val vel = entity.get[VelComponent]
    val stats = entity.get[ShipStatsComponent]

    dir.dir = ((dir.dir + 2*math.Pi) % (math.Pi * 2)).toFloat

    val targetShip = Try(ships.asScala
      .filter(ship => ship.get[IdComponent].id != projectile.ownerId)
      .minBy(rateTargetShip(pos, dir, _))).toOption

    val dirToShip = targetShip
      .map(
        ship => getDirTo(pos, ship.get[PosComponent])
      )
      .getOrElse(dir.dir)

    if (dir.dir < dirToShip) {
      if (dirToShip - dir.dir < math.Pi) {
        dir.dir += stats.turn
      } else {
        dir.dir -= stats.turn
      }
    } else if (dir.dir > dirToShip) {
      if (dir.dir - dirToShip < math.Pi) {
        dir.dir -= stats.turn
      } else {
        dir.dir += stats.turn
      }
    }

    if (math.abs(dir.dir - dirToShip) < stats.turn) {
      dir.dir = dirToShip.toFloat
    }

    vel.dx += stats.thrust * Utils.cos(dir.dir)
    vel.dy += stats.thrust * Utils.sin(dir.dir)
  }

  def getDirTo(from: PosComponent, to: PosComponent): Float =
    ((math.atan2(to.y-from.y, to.x-from.x) + 2*math.Pi) % (2*math.Pi)).toFloat

  def rateTargetShip(pos: PosComponent, dir: DirComponent, ship: Entity): Double = {
    val sPos = ship.get[PosComponent]
    math.pow(pos.x-sPos.x, 2) + math.pow(pos.y-sPos.y, 2)
  }
}
