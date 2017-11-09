package me.shreyasr.networking

import com.badlogic.ashley.core.Entity
import me.shreyasr.networking.component._

object EntityFactory {

  def newPlayer = new Entity()
    .add(new IdComponent(1))
    .add(new PosComponent(4, 7))
    .add(new VelComponent(0, 0))
    .add(new InputComponent(false, false, false, false, false, false))
    .add(new DirComponent(0))
    .add(new ShipStatsComponent(0.2f, 0.06f, 6f, 100))
    .add(new DrawingComponent(
           List(
             PolarLine(PolarPoint(0, 30), PolarPoint(140, 30)),
             PolarLine(PolarPoint(0, 30), PolarPoint(220, 30)),
             PolarLine(PolarPoint(140, 30), PolarPoint(220, 30))
           )))
    .add(new CameraFocusComponent)

  def newImmobile = new Entity()
    .add(new IdComponent(2))
    .add(new PosComponent(700, 700))
    .add(new VelComponent(0, 0))
    .add(new DirComponent(0))
    .add(new ShipStatsComponent(0.1f, 0.03f, 0f, 10))
    .add(new DrawingComponent(
           List(
             PolarLine(PolarPoint(0, 30), PolarPoint(140, 30)),
             PolarLine(PolarPoint(0, 30), PolarPoint(220, 30)),
             PolarLine(PolarPoint(140, 30), PolarPoint(220, 30))
           )))

  def newLaser(parentId: Long, pos: PosComponent, dir: Float) = new Entity()
    .add(new ProjectileComponent(parentId))
    .add(new PosComponent(pos.x, pos.y))
    .add(new VelComponent(12*Utils.cos(dir), 12*Utils.sin(dir)))
    .add(new DirComponent(dir))
    .add(new DrawingComponent(
           List(
             PolarLine(PolarPoint(0, 10), PolarPoint(180, 10))
           )))


  def newMissile(parentId: Long, pos: PosComponent, dir: Float) = new Entity()
    .add(new ProjectileComponent(parentId))
    .add(new PosComponent(pos.x, pos.y))
    .add(new VelComponent(0, 0))
    .add(new DirComponent(dir))
    .add(new ShipStatsComponent(0.4f, 0.1f, 8.5f, 1))
    .add(new DrawingComponent(
           List(
             PolarLine(PolarPoint(0, 20), PolarPoint(160, 20)),
             PolarLine(PolarPoint(0, 20), PolarPoint(200, 20)),
             PolarLine(PolarPoint(160, 20), PolarPoint(200, 20))
           )))
}
