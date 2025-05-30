package spotify

import common.{ApiClient, Config}

import java.nio.file.{Files, Paths}
import java.time.Instant

import scala.io.Source
import java.nio.charset.StandardCharsets
import scala.util.{Failure, Success}

object AuthService {
    private var token: Option[String] = None
    private var expiry: Instant = Instant.now()

    def getToken: String = {
        if (token.isEmpty || Instant.now().isAfter(expiry)) {
            readTokenFromFile()
            if (token.isEmpty || Instant.now().isAfter(expiry)) {
                refreshToken()
            }
        }
        token.get
    }

    private def readTokenFromFile(): Unit = {
        if (Files.exists(Paths.get(Config.tokenFile))) {
            val source = Source.fromFile(Config.tokenFile)
            try {
                val lines = source.getLines().toList
                val tokenLine = lines.find(_.startsWith("access_token="))
                val expiryLine = lines.find(_.startsWith("expiry="))

                token = tokenLine.map(_.split("=")(1).trim)
                expiry = expiryLine.map(line => Instant.parse(line.split("=")(1).trim)).getOrElse(Instant.now())
            } finally {
                source.close()
            }
        }
    }

    private def refreshToken(): Unit = {
        val authString = java.util.Base64.getEncoder.encodeToString(
            s"${Config.clientId}:${Config.clientSecret}".getBytes(StandardCharsets.UTF_8)
        )
        val headers = Map(
            "Authorization" -> s"Basic $authString",
            "Content-Type" -> "application/x-www-form-urlencoded"
        )
        val data = Map("grant_type" -> "client_credentials")
        ApiClient.post("https://accounts.spotify.com/api/token", headers, data) match {
            case Success(response) =>
                val json = ujson.read(response)
                token = Some(json("access_token").str)
                expiry = Instant.now().plusSeconds(json("expires_in").num.toLong)
                writeTokenToFile()
            case Failure(exception) => throw exception
        }

    }

    private def writeTokenToFile(): Unit = {
        val content = s"access_token=${token.get}\nexpiry=${expiry.toString}"
        Files.write(Paths.get(Config.tokenFile), content.getBytes(StandardCharsets.UTF_8))
    }
}

