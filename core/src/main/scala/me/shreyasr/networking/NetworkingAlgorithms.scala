package me.shreyasr.networking

import java.util.Arrays
import me.shreyasr.networking.network.Packet
import scala.collection.JavaConverters._

import com.badlogic.ashley.core.{Engine, Entity, Family}
import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.shreyasr.networking.component._
import me.shreyasr.networking.network.{GameStatePacket, SerializationStream, Socket}
import me.shreyasr.networking.system._

class NetworkingAlgorithms(val socket: Socket) extends ApplicationAdapter {

  val engine = new Engine
  lazy val shapeRenderer = { val s = new ShapeRenderer; s.setAutoShapeType(true); s };
  lazy val camera = new OrthographicCamera()
  lazy val viewport = new ExtendViewport(1280*1.2f, 700*1.2f,
                                         1280*1.2f, 1000*1.2f, camera)

  def player = engine.getEntitiesFor(
    Family.all(classOf[CameraFocusComponent]).get).first()

  override def resize(width: Int, height: Int) = viewport.update(width, height)

  override def create() = {
    engine.addEntity(EntityFactory.newPlayer)
    engine.addEntity(EntityFactory.newImmobile)

    val priority = { var i = 0; () => { i += 1; i} }
    engine.addSystem(new InputSystem(priority(), this))
    engine.addSystem(new InputProcessingSystem(priority(), this))
    engine.addSystem(new MissileLogicUpdateSystem(priority(), this))
    engine.addSystem(new VelocityUpdateSystem(priority(), this))
    engine.addSystem(new CollisionSystem(priority(), this))
    engine.addSystem(new HealthCheckSystem(priority(), this))
  }


  var cnt = 0;
  val buffer = new Array[Byte](32184)
  override def render() = {
    cnt += 1
    // socket.send(new InputPacket(player.get[InputComponent]))
    engine.update(1);
    drawAll();

    val gameState = new GameStatePacket(engine.getEntities.asScala.toList)
    val writeStream = new SerializationStream(buffer)
    Packet.writePacket(writeStream, gameState)

    //println(buffer.take(writeStream.getPos).mkString(" "))
    println(s"Bytes: ${writeStream.getPos}")

    val readStream = new SerializationStream(buffer, writeStream.getPos)
    val newGameState = Packet.readPacket(readStream).asInstanceOf[GameStatePacket]

    engine.removeAllEntities()

    newGameState.entities.foreach(
      engine.addEntity(_))
  }

  private def drawAll() = {
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
