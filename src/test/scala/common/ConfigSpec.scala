package common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFlatSpec with Matchers {

    it should "load the CACHE_PATH from the .env file" in {
        Config.tokenFile should be ("cache/token.txt")
    }
}
