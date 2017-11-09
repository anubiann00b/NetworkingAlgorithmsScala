package me.shreyasr.networking.system

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import me.shreyasr.networking._
import me.shreyasr.networking.component._

class HealthCheckSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family.all(classOf[ShipStatsComponent]).get, p) {

  override def processEntity(entity: Entity, delta: Float) = {
    if (entity.get[ShipStatsComponent].health <= 0) {
      getEngine.removeEntity(entity)
    }
  }
}
