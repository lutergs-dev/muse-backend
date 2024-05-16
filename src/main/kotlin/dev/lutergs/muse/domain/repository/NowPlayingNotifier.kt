package dev.lutergs.muse.domain.repository

import dev.lutergs.muse.domain.entity.User

interface NowPlayingNotifier {
  fun notify(user: User)
}