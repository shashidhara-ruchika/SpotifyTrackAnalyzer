package spotify.dao

case class Artist(id: String, name: String, followers_count: Int)

object Artist {
    implicit val rw: upickle.default.ReadWriter[Artist] = upickle.default.macroRW
}