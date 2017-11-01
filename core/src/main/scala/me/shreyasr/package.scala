package me.shreyasr

import com.badlogic.ashley.core.{ Component, Entity }
import scala.Option
import scala.reflect.{ ClassTag, _ }

package object networking {

  implicit class EntityImprovements(val entity: Entity) {
    def has[T <: Component: ClassTag]: Boolean = getOpt[T].isDefined
    def getOpt[T <: Component: ClassTag]: Option[T] = Option(get[T])
    def get[T <: Component: ClassTag]: T =
      entity.getComponent(classTag[T].runtimeClass.asInstanceOf[Class[T]])
    def remove[T <: Component: ClassTag]: Option[Component] =
      Option(entity.remove(classTag[T].runtimeClass.asInstanceOf[Class[T]]))
  }

  implicit class FloatImprovements(val float: Float) {
    def radians = math.toRadians(float.toDouble).toFloat
  }
}
