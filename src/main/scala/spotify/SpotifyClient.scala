package spotify

import common.ApiClient
import spotify.dao.{Artist, PlaylistTracksPart, Track}
import upickle.default.read

import scala.util.{Failure, Success}

object SpotifyClient {

    def getPlayListTracks(playListId: String, offset: Int = 0, limit: Int = 25): Option[PlaylistTracksPart] = {
        println(s"[INFO] Fetching playlist tracks for $playListId with offset $offset and limit $limit")
        val playListUrl = s"https://api.spotify.com/v1/playlists/$playListId/tracks?offset=$offset&limit=$limit"
        val response = ApiClient.get(
            playListUrl,
            headers = Map("Authorization" -> s"Bearer ${AuthService.getToken}")
        )

        response match {
            case Success(body) =>
                val json = ujson.read(body)
                val tracks = json("items").arr.map {
                    item =>
                        val trackJson = item("track")
                        val artistIds = trackJson("artists").arr.map(artist => artist("id").str).toList
                        Track(
                            trackJson("id").str,
                            trackJson("name").str,
                            trackJson("duration_ms").num.toInt,
                            artistIds
                        )
                }.toList
                val playlistPart = PlaylistTracksPart(
                    playListId,
                    json("offset").num.toInt,
                    json("limit").num.toInt,
                    json("total").num.toInt,
                    tracks
                )
                Some(playlistPart)
            case Failure(_) => None
        }
    }

    def getArtist(artistId: String): Option[Artist] = {
        println(s"[INFO] Fetching artist details for $artistId")
        val artistUrl = s"https://api.spotify.com/v1/artists/$artistId"
        val response = ApiClient.get(
            artistUrl,
            headers = Map("Authorization" -> s"Bearer ${AuthService.getToken}")
        )

        response match {
            case Success(body) =>
                val json = ujson.read(body)
                val artist = Artist(
                    json("id").str,
                    json("name").str,
                    json("followers")("total").num.toInt
                )
                Some(artist)
            case Failure(_) => None
        }
    }
}
