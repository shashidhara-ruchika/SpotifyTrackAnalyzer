package spotify.dao

case class PlaylistTracksPart(playListId: String, offset: Int, limit: Int, total: Int, tracks: List[Track])

object PlaylistTracksPart {
    implicit val rw: upickle.default.ReadWriter[PlaylistTracksPart] = upickle.default.macroRW
}
