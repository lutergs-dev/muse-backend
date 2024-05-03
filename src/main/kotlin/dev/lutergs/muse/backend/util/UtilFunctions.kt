package dev.lutergs.muse.backend.util

import java.util.*


fun generateRandomString(): String {
  val charPool: List<Char> = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '_'
  val stringLength = 30
  return (1..stringLength)
    .map { Random().nextInt(charPool.size) }
    .map { charPool[it] }
    .joinToString("")
}