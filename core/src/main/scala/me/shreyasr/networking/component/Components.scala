package me.shreyasr.networking.component;

import com.badlogic.ashley.core.Component
import me.shreyasr.networking.Utils
import me.shreyasr.networking._
import me.shreyasr.networking.network.SerializationStream

sealed trait Components extends Component {
  def write(stream: SerializationStream)
}

final class IdComponent(var id: Long) extends Components {
 def this(stream: SerializationStream) = {
    this(stream.readLong())
  }

  def write(stream: SerializationStream) = {
    stream.writeLong(id)
  }
}

final class InputComponent(var thrust: Boolean, var reverseThrust: Boolean,
                           var turnCw: Boolean, var turnCcw: Boolean,
                           var fireLaser: Boolean,
                           var fireMissile: Boolean) extends Components {

  def this(stream: SerializationStream) = {
    this(stream.readBool(0), stream.readBool(1), stream.readBool(2),
         stream.readBool(3), stream.readBool(4), stream.readBool(5));
    stream.skipByte();
  }

  def write(stream: SerializationStream) = {
    stream.writeBools(thrust, reverseThrust, turnCw, turnCcw,
                      fireLaser, fireMissile, false, false)
  }

  override def toString = "(" +
    (if (thrust) "W" else " ") +
    (if (reverseThrust) "S" else " ") +
    (if (turnCcw) "A" else " ") +
    (if (turnCw) "D" else " ") +
    (if (fireLaser) "L" else " ") +
    (if (fireMissile) "M" else " ") +
    ")"
}

final class PosComponent(var x: Float, var y: Float) extends Components {
  def this(stream: SerializationStream) = {
    this(stream.readFloat(), stream.readFloat())
  }

  def write(stream: SerializationStream) = {
    stream.writeFloat(x)
    stream.writeFloat(y)
  }
}

final class VelComponent(var dx: Float, var dy: Float) extends Components {
  def this(stream: SerializationStream) = {
    this(stream.readFloat(), stream.readFloat())
  }

  def write(stream: SerializationStream) = {
    stream.writeFloat(dx)
    stream.writeFloat(dy)
  }

  def magnitude = math.sqrt(dx*dx + dy*dy).toFloat
}

// All dirs are in radians
final class DirComponent(var dir: Float) extends Components {
  def this(stream: SerializationStream) = {
    this(stream.readFloat())
  }

  def write(stream: SerializationStream) = {
    stream.writeFloat(dir)
  }
}

final class ShipStatsComponent(var thrust: Float, var turn: Float,
                               var maxSpeed: Float,
                               var maxHealth: Int) extends Components {
  var health = maxHealth

  def this(stream: SerializationStream) = {
    this(stream.readFloat(), stream.readFloat(),
         stream.readFloat(), stream.readInt())
  }

  def write(stream: SerializationStream) = {
    stream.writeFloat(thrust)
    stream.writeFloat(turn)
    stream.writeFloat(maxSpeed)
    stream.writeInt(maxHealth)
  }
}

final class DrawingComponent(var linesPolar: List[PolarLine]) extends Components {
  def this(stream: SerializationStream) = {
    this(DrawingComponent.getPolarLineListFromStream(stream))
  }

  def write(stream: SerializationStream) = {
    stream.writeInt(linesPolar.length)
    linesPolar.foreach(_.write(stream))
  }
}
object DrawingComponent {
  def getPolarLineListFromStream(stream: SerializationStream): List[PolarLine] = {
    val numLines = stream.readInt()
    return (1 to numLines)
      .map(_ => new PolarLine(stream))
      .toList
  }
}

final class CameraFocusComponent extends Components {
  def write(stream: SerializationStream) = {
    
  }
}

final class ProjectileComponent(var ownerId: Long) extends Components {
  def this(stream: SerializationStream) = {
    this(stream.readLong())
  }

  def write(stream: SerializationStream) = {
    stream.writeLong(ownerId)
  }
}

case class PolarLine(start: PolarPoint, end: PolarPoint) {
  def this(stream: SerializationStream) =
    this(new PolarPoint(stream), new PolarPoint(stream))

  def write(stream: SerializationStream) = {
    start.write(stream)
    end.write(stream)
  }

  def getCartesian(dx: Float, dy: Float, dir: Float) = {
    (
      dx + start.r * Utils.cos(dir+start.theta.radians),
      dy + start.r * Utils.sin(dir+start.theta.radians),
      dx + end.r * Utils.cos(dir+end.theta.radians),
      dy + end.r * Utils.sin(dir+end.theta.radians)
    )
  }
}
case class PolarPoint(theta: Float, r: Float) {
 def this(stream: SerializationStream) =
   this(stream.readFloat(), stream.readFloat())

  def write(stream: SerializationStream) = {
    stream.writeFloat(theta)
    stream.writeFloat(r)
  }
}
