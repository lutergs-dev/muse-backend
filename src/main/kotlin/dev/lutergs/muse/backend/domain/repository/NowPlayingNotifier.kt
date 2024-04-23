package dev.lutergs.muse.backend.domain.repository

import dev.lutergs.muse.backend.domain.entity.User

interface NowPlayingNotifier {
  fun notify(user: User)
}