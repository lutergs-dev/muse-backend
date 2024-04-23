package dev.lutergs.muse.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MuseBackendApplication

fun main(args: Array<String>) {
  runApplication<MuseBackendApplication>(*args)
}
