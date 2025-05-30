package common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}

class ApiClientSpec extends AnyFlatSpec with Matchers with MockFactory {

    "ApiClient" should "successfully perform a GET request" in {
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        val response = ApiClient.get(url)

        response match {
            case Success(body) =>
                body should include ("userId")
                body should include ("id")
                body should include ("title")
                body should include ("body")
            case Failure(exception) =>
                fail(s"GET request failed: ${exception.getMessage}")
        }
    }

    it should "successfully perform a POST request" in {
        val url = "https://jsonplaceholder.typicode.com/posts"
        val data = Map("title" -> "foo", "body" -> "bar", "userId" -> "1")
        val response = ApiClient.post(url, data = data)

        response match {
            case Success(body) =>
                body should include ("id")
                body should include ("title")
                body should include ("body")
                body should include ("userId")
            case Failure(exception) =>
                fail(s"POST request failed: ${exception.getMessage}")
        }
    }
}