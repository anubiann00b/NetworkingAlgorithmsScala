package me.shreyasr.networking.component;

import com.badlogic.ashley.core.Component
import me.shreyasr.networking.Utils
import me.shreyasr.networking._

sealed class Components extends Component
final class IdComponent(var id: Long) extends Components
final class InputComponent(var thrust: Boolean, var reverseThrust: Boolean,
                           var turnCw: Boolean, var turnCcw: Boolean,
                           var fireLaser: Boolean,
                           var fireMissile: Boolean) extends Components
final class PosComponent(var x: Float, var y: Float) extends Components
final class VelComponent(var dx: Float, var dy: Float) extends Components {
  def magnitude = math.sqrt(dx*dx + dy*dy).toFloat
}
// All dirs are in radians
final class DirComponent(var dir: Float) extends Components
final class ShipStatsComponent(var thrust: Float, var turn: Float,
                               var maxSpeed: Float,
                               var maxHealth: Int) extends Components {
  var health = maxHealth
}
final class DrawingComponent(var linesPolar: List[PolarLine]) extends Components
final class CameraFocusComponent extends Components
final class ProjectileComponent(var ownerId: Long) extends Components

case class PolarLine(start: PolarPoint, end: PolarPoint) {
  def getCartesian(dx: Float, dy: Float, dir: Float) = {
    (
      dx + start.r * Utils.cos(dir+start.theta.radians),
      dy + start.r * Utils.sin(dir+start.theta.radians),
      dx + end.r * Utils.cos(dir+end.theta.radians),
      dy + end.r * Utils.sin(dir+end.theta.radians)
    )
  }
}
case class PolarPoint(theta: Float, r: Float)
