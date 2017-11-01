package me.shreyasr.networking.component;

import com.badlogic.ashley.core.Component

sealed class Components extends Component
final class InputComponent(var thrust: Boolean, var reverseThrust: Boolean,
                           var turnCw: Boolean, var turnCcw: Boolean) extends Components
final class PosComponent(var x: Float, var y: Float) extends Components
final class VelComponent(var dx: Float, var dy: Float) extends Components
final class DirComponent(var dir: Float) extends Components
final class ShipStatsComponent(var thrust: Float, var turn: Float) extends Components
final class DrawingComponent(var radialPoints: List[Tuple2[Float, Float]]) extends Components
