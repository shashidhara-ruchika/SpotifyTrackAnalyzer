package spotify

import common.Config
import spotify.dao.{Artist, Track}

import scala.annotation.tailrec
import scala.collection.immutable.SortedSet

object SpotifyAnalyzer {

    implicit val trackOrdering: Ordering[Track] = Ordering.by(-_.duration_ms)

    implicit val artistOrdering: Ordering[Artist] = Ordering.by(-_.followers_count)

    def getTopNLongestTracks(playlistId: String, n: Int): Option[List[Track]] = {

        @tailrec
        def inner(offset: Int, totalTracks: Int, topTracks: SortedSet[Track]): Option[List[Track]] = {
            if (offset >= totalTracks) {
                Some(topTracks.toList)
            } else {
                SpotifyClient.getPlayListTracks(playlistId, offset, Config.trackProcessorBatchSize) match {
                    case Some(part) =>
                        val updatedTopTracks = part.tracks.foldLeft(topTracks) { (acc, track) =>
                            val newAcc = acc + track
                            if (newAcc.size > n) newAcc.dropRight(1) else newAcc
                        }
                        inner(offset + Config.trackProcessorBatchSize, part.total, updatedTopTracks)
                    case None =>
                        println("Failed to fetch playlist tracks.")
                        None
                }
            }
        }

        inner(0, Int.MaxValue, SortedSet.empty)
    }

    def getUniqueArtistIdsFromTracks(tracks: List[Track]): List[String] = {
        tracks.flatMap(_.artist_ids).distinct
    }

    def getRankedArtistsByFollowersCount(artistIds: List[String]): List[Artist] = {
        artistIds.map(SpotifyClient.getArtist).collect {
            case Some(artist) => artist
        }.sorted
    }


    def main(args: Array[String]): Unit = {
        if (args.length < 1) {
            println("Usage: TrackProcessor <playlist_id>")
            return
        }
        val playlistId = args(0)

        val topTracks = getTopNLongestTracks(playlistId, 10)

        println(f"\n${"Song name"}%-50s ${"Duration (ms)"}")
        topTracks.get.foreach { track =>
            println(f"${track.name}%-50s ${track.duration_ms}")
        }
        println()

        val artistIds = getUniqueArtistIdsFromTracks(topTracks.get)

        val rankedArtists = getRankedArtistsByFollowersCount(artistIds)

        println(f"\n${"Artist"}%-30s ${"Followers (count)"}")
        rankedArtists.foreach { artist =>
            println(f"${artist.name}%-30s ${artist.followers_count}%10d")
        }
        println()
    }

}