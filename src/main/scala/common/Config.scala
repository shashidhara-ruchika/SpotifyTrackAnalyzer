package common

import scala.io.Source

object Config {
    private val envVars: Map[String, String] = {
        val source = Source.fromFile(".env")
        val lines = source.getLines().filter(_.contains("=")).map { line =>
            val parts = line.split("=")
            parts(0).trim -> parts(1).trim
        }.toMap
        source.close()
        lines
    }

    val clientId: String = envVars.getOrElse("SPOTIFY_CLIENT_ID", "")
    val clientSecret: String = envVars.getOrElse("SPOTIFY_CLIENT_SECRET", "")
    val tokenFile: String = envVars.getOrElse("TOKEN_FILE", "")
    val trackProcessorBatchSize: Int = envVars.getOrElse("TRACK_PROCESSOR_BATCH_SIZE", "30").toInt
    val trackProcessorBatchDuration: Int = envVars.getOrElse("TRACK_PROCESSOR_BATCH_DURATION", "2").toInt
}
