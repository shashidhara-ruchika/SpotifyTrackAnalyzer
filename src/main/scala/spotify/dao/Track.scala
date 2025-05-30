package spotify.dao

case class Track(id: String, name: String, duration_ms: Int, artist_ids: List[String])

object Track {
    implicit val rw: upickle.default.ReadWriter[Track] = upickle.default.macroRW
}