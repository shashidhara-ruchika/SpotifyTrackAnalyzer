package common

import scala.util.Try

object ApiClient {

    def get(url: String, headers: Map[String, String] = Map.empty): Try[String] = {
        Try {
            val response = requests.get(url, headers = headers)
            if (response.is2xx) {
                response.text()
            } else {
                throw new RuntimeException(s"GET request failed: ${response.statusCode} ${response.text()}")
            }
        }
    }

    def post(url: String, headers: Map[String, String] = Map.empty, data: Map[String, String] = Map.empty): Try[String] = {
        Try {
            val response = requests.post(url, headers = headers, data = data)
            if (response.is2xx) {
                response.text()
            } else {
                throw new RuntimeException(s"POST request failed: ${response.statusCode} ${response.text()}")
            }
        }
    }
}
