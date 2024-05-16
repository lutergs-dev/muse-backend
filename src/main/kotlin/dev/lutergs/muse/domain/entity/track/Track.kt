package dev.lutergs.muse.domain.entity.track

data class Track (
  val vendor: MusicVendor,
  val uid: String
) {

  companion object {
    fun nullTrack(): Track = Track(
      vendor = MusicVendor.Else,
      uid = ""
    )
  }
}
