package me.shreyasr.networking.system

import com.badlogic.ashley.core.{ Engine, Entity, Family }
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{ Intersector, Vector2 }
import me.shreyasr.networking._
import me.shreyasr.networking.component._

import scala.collection.JavaConverters._

class CollisionSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family
    .all(
      classOf[IdComponent],
      classOf[PosComponent],
      classOf[DirComponent],
      classOf[DrawingComponent],
      classOf[ShipStatsComponent])
    .exclude(
      classOf[ProjectileComponent]).get, p) {

  var projectiles: ImmutableArray[Entity] = null

  override def addedToEngine(engine: Engine) = {
    super.addedToEngine(engine)
    projectiles = getEngine.getEntitiesFor(
      Family
        .all(
          classOf[ProjectileComponent],
          classOf[DrawingComponent],
          classOf[DirComponent],
          classOf[PosComponent]).get
    )
  }

  override def removedFromEngine(engine: Engine) = {
    super.removedFromEngine(engine)
    projectiles = null;
  }

  override def processEntity(entity: Entity, delta: Float) = {
    val eId = entity.get[IdComponent]
    val eDir = entity.get[DirComponent]
    val ePos = entity.get[PosComponent]
    val eDrawing = entity.get[DrawingComponent]
    val eStats = entity.get[ShipStatsComponent]


    projectiles.asScala
      .filter(
        projectile => eId.id != projectile.get[ProjectileComponent].ownerId)
      .filter(
        projectile => {
          val pDir = projectile.get[DirComponent]
          val pPos = projectile.get[PosComponent]
          val pDrawing = projectile.get[DrawingComponent]

          val eLines = eDrawing.linesPolar
            .map(_.getCartesian(ePos.x, ePos.y, eDir.dir))

          val pLines = pDrawing.linesPolar
            .map(_.getCartesian(pPos.x, pPos.y, pDir.dir))

          val linePairs = for (a <- eLines; b <- pLines) yield (a, b)

          linePairs.exists{
            case ((ex1, ey1, ex2, ey2), (px1, py1, px2, py2)) => {
              Intersector.intersectSegments(ex1, ey1, ex2, ey2, px1, py1, px2, py2, null)
            }
          }
        })
      .foreach(
        projectile => {
          println("Goodbye, cruel world!")
          eStats.health -= 1
          getEngine.removeEntity(projectile)
        })
  }
}
