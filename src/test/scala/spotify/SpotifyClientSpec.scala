package spotify

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spotify.dao.{Artist, PlaylistTracksPart, Track}

class SpotifyClientSpec extends AnyFlatSpec with Matchers {

    it should "fetch playlist tracks successfully" in {
        val playListId = "5Rrf7mqN8uus2AaQQQNdc1"
        val offset = 0
        val limit = 25

        val optionalTracks: Option[PlaylistTracksPart] = SpotifyClient.getPlayListTracks(playListId, offset, limit)

        optionalTracks match {
            case Some(playlistTracksPart: PlaylistTracksPart) =>
                playlistTracksPart.playListId should not be empty
                playlistTracksPart.offset should be >= 0
                playlistTracksPart.limit should be > 0
                playlistTracksPart.total should be >= 0
                playlistTracksPart.tracks should not be empty
                playlistTracksPart.tracks.foreach { track =>
                    track.id should not be empty
                    track.name should not be empty
                    track.duration_ms should be > 0
                    track.artist_ids should not be empty
                }
            case None => fail("Failed to fetch playlist tracks")
        }
    }

    it should "fetch artist successfully" in {
        val artistId = "3koiLjNrgRTNbOwViDipeA"

        val optionalArtists: Option[Artist] = SpotifyClient.getArtist(artistId)

        optionalArtists match {
            case Some(artist) =>
                artist.id should not be empty
                artist.name should not be empty
                artist.followers_count should be > 0
            case None => fail("Failed to fetch artist")
        }
    }
}