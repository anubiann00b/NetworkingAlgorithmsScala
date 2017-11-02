package me.shreyasr.networking

object Utils {

  def cos(num: Float) = math.cos(num.toDouble).toFloat
  def sin(num: Float) = math.sin(num.toDouble).toFloat

  def clamp(num: Float, lo: Float, hi: Float) =
    if (num < lo) lo else if (num > hi) hi else num
}
