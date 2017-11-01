package me.shreyasr.networking

import scala.collection.JavaConverters._

import com.badlogic.ashley.core.{Engine, Entity, Family}
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.shreyasr.networking._
import me.shreyasr.networking.component._
import me.shreyasr.networking.system._

class NetworkingAlgorithms extends ApplicationAdapter {

  val engine = new Engine
  lazy val shapeRenderer = { val s = new ShapeRenderer; s.setAutoShapeType(true); s };
  lazy val camera = new OrthographicCamera()
  lazy val viewport = new ExtendViewport(1280/1f, 700/1f, 1280/1f, 1000/1f, camera)

  override def resize(width: Int, height: Int) = viewport.update(width, height)

  override def create() = {
    engine.addEntity(new Entity()
                       .add(new PosComponent(4, 7))
                       .add(new VelComponent(0, 0))
                       .add(new InputComponent(false, false, false, false))
                       .add(new DirComponent(0))
                       .add(new ShipStatsComponent(0.1f, 0.03f))
                       .add(new DrawingComponent(
                              List(
                                (0, 30),
                                (140, 30),
                                (220, 30)
                              )))
    )

    val p = { var i = 0; () => { i += 1; i} }
    engine.addSystem(new InputSystem(p(), this))
    engine.addSystem(new InputProcessingSystem(p(), this))
    engine.addSystem(new VelocityUpdateSystem(p(), this))
}

  override def render() = {
    engine.update(1);

    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    camera.position.set(0, 0, 0);
    viewport.apply()
    camera.update()

    shapeRenderer.setProjectionMatrix(camera.combined)
    shapeRenderer.begin()

    engine.getEntitiesFor(Family.all(
                            classOf[PosComponent],
                            classOf[DrawingComponent],
                            classOf[DirComponent]).get())
      .asScala
      .foreach(drawEntity(_))

    shapeRenderer.end()
  }

  private def drawEntity(entity: Entity) = {
    val pos = entity.get[PosComponent]
    val dir = entity.get[DirComponent].dir
    val points = entity.get[DrawingComponent].radialPoints
    points.zip(points.tail ++ points.headOption).toList
      .foreach{case ((theta1, r1), (theta2, r2)) => {
                 val x1 = pos.x + r1 * Utils.cos(dir+theta1.radians)
                 val y1 = pos.y + r1 * Utils.sin(dir+theta1.radians)
                 val x2 = pos.x + r2 * Utils.cos(dir+theta2.radians)
                 val y2 = pos.y + r2 * Utils.sin(dir+theta2.radians)
                 shapeRenderer.line(x1, y1, x2, y2)
               }}
  }
}
