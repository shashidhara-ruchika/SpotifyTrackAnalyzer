package spotify

import common.Config
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import spotify.dao.{Artist, Track}

object TrackProcessor {

    def main(args: Array[String]): Unit = {

        if (args.length < 1) {
            println("Usage: TrackProcessor <playlist_id>")
            return
        }
        val playlistId = args(0)

        val spark = SparkSession.builder()
            .appName("SpotifyTrackProcessor")
            .master("local[*]")
            .getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        import spark.implicits._

        var offset = 0
        val limit = Config.trackProcessorBatchSize

        // Initialize an empty DataFrame
        var tracksDF = spark.emptyDataset[Track].toDF()

        var totalTracks = Int.MaxValue
        while (offset < totalTracks) {
            SpotifyClient.getPlayListTracks(playlistId, offset, limit) match {
                case Some(part) =>
                    val batchDF = part.tracks.toDF()
                    tracksDF = tracksDF.union(batchDF)
                    totalTracks = part.total
                    offset += limit
                case None =>
                    println("Failed to fetch playlist tracks.")
                    return
            }
        }

        val topTracksDF = tracksDF.orderBy(desc("duration_ms")).limit(10)

        topTracksDF.select("name", "duration_ms").show(false)
        println("[Assignment] Top 10 Tracks")

        val artistIdsDF = topTracksDF.select(explode(col("artist_ids")).as("artist_id")).distinct()

        // Initialize an empty DataFrame for artists
        var artistsDF = spark.emptyDataset[Artist].toDF()

        artistIdsDF.collect().foreach { row =>
            val artistId = row.getString(0)
            SpotifyClient.getArtist(artistId) match {
                case Some(artist) =>
                    val artistDF = Seq(artist).toDF()
                    artistsDF = artistsDF.union(artistDF)
                case None => println(s"Failed to fetch artist details for $artistId")
            }
        }

        val rankedArtistsDF = artistsDF.orderBy(desc("followers_count"))

        rankedArtistsDF.select("name", "followers_count").show(false)
        println("[Assignment] Artists by follower count of Top 10 Tracks")

        spark.stop()
    }
}