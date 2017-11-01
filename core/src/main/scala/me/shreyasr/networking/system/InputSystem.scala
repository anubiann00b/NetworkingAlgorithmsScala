package me.shreyasr.networking.system

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import me.shreyasr.networking._
import me.shreyasr.networking.component._

class InputSystem(p: Int, val game: NetworkingAlgorithms) extends IteratingSystem(
  Family.all(classOf[InputComponent]).get, p) {

  override def processEntity(entity: Entity, delta: Float) = {
    val input = entity.get[InputComponent]
    input.thrust = Gdx.input.isKeyPressed(Keys.W)
    input.reverseThrust = Gdx.input.isKeyPressed(Keys.S)
    input.turnCw = Gdx.input.isKeyPressed(Keys.D)
    input.turnCcw = Gdx.input.isKeyPressed(Keys.A)
  }
}
