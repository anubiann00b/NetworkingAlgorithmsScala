package me.shreyasr.networking

import scala.collection.JavaConverters._

import com.badlogic.ashley.core.{Engine, Entity, Family}
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.shreyasr.networking.component._
import me.shreyasr.networking.system._

class NetworkingAlgorithms extends ApplicationAdapter {

  val engine = new Engine
  lazy val shapeRenderer = { val s = new ShapeRenderer; s.setAutoShapeType(true); s };
  lazy val camera = new OrthographicCamera()
  lazy val viewport = new ExtendViewport(1280*1.2f, 700*1.2f, 1280*1.2f, 1000*1.2f, camera)

  override def resize(width: Int, height: Int) = viewport.update(width, height)

  override def create() = {
    engine.addEntity(new Entity()
                       .add(new IdComponent(1))
                       .add(new PosComponent(4, 7))
                       .add(new VelComponent(0, 0))
                       .add(new InputComponent(false, false, false, false, false))
                       .add(new DirComponent(0))
                       .add(new ShipStatsComponent(0.1f, 0.03f, 10, 10))
                       .add(new DrawingComponent(
                              List(
                                PolarLine(PolarPoint(0, 30), PolarPoint(140, 30)),
                                PolarLine(PolarPoint(0, 30), PolarPoint(220, 30)),
                                PolarLine(PolarPoint(140, 30), PolarPoint(220, 30))
                              )))
                       .add(new CameraFocusComponent)
    )

    engine.addEntity(new Entity()
                       .add(new IdComponent(2))
                       .add(new PosComponent(700, 700))
                       .add(new VelComponent(0, 0))
                       .add(new DirComponent(0))
                       .add(new ShipStatsComponent(0.1f, 0.03f, 10, 10))
                       .add(new DrawingComponent(
                              List(
                                PolarLine(PolarPoint(0, 30), PolarPoint(140, 30)),
                                PolarLine(PolarPoint(0, 30), PolarPoint(220, 30)),
                                PolarLine(PolarPoint(140, 30), PolarPoint(220, 30))
                              )))
    )

    val priority = { var i = 0; () => { i += 1; i} }
    engine.addSystem(new InputSystem(priority(), this))
    engine.addSystem(new InputProcessingSystem(priority(), this))
    engine.addSystem(new VelocityUpdateSystem(priority(), this))
    engine.addSystem(new CollisionSystem(priority(), this))
}

  override def render() = {
    engine.update(1);

    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    engine.getEntitiesFor(
      Family.all(classOf[CameraFocusComponent], classOf[PosComponent]).get).asScala
      .headOption
      .map(_.get[PosComponent])
      .foreach(pos => {
                 val paddingX = viewport.getWorldWidth / 2
                 val paddingY = viewport.getWorldHeight / 2
                 camera.position.set(
                   Utils.clamp(pos.x, paddingX, 4000 - paddingX),
                   Utils.clamp(pos.y, paddingY, 4000 - paddingY), 0)
               })

    viewport.apply()
    camera.update()

    shapeRenderer.setProjectionMatrix(camera.combined)
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

    shapeRenderer.setColor(1, 1, 1, 1)
    (0 to 4000 by 500).foreach(x => shapeRenderer.line(x.toFloat, 0, x.toFloat, 4000))
    (0 to 4000 by 500).foreach(y => shapeRenderer.line(0, y.toFloat, 4000, y.toFloat))

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
    val lines = entity.get[DrawingComponent].linesPolar

    val damage = entity.getOpt[ShipStatsComponent]
      .map(s => s.health.toFloat / s.maxHealth)
      .map(Utils.clamp(_, 0, 1))
      .getOrElse(1f)

    shapeRenderer.setColor(1, damage, damage, 1)

    lines
      .map(_.getCartesian(pos.x, pos.y, dir))
      .foreach{case (x1, y1, x2, y2) => { shapeRenderer.line(x1, y1, x2, y2) }}
  }
}
