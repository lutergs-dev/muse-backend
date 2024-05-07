package dev.lutergs.muse.backend.util

import java.util.*


fun generateRandomString(length: Int = 30): String {
  val charPool: List<Char> = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '_'
  return (1..length)
    .map { Random().nextInt(charPool.size) }
    .map { charPool[it] }
    .joinToString("")
}