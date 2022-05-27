package ez.vertx.core.config

import io.vertx.core.http.HttpServerOptions

class HttpServerConfig : HttpServerOptions() {
  /**
   * always use http 200 status code in response header
   */
  var alwaysUseOkStatus = false

  /**
   * max request handling time(milliseconds)
   */
  var timeout = 10000L

  /**
   * you should pass this queryParam(name is `_apiKey`) when accessing admin resource or manually executing sql
   */
  var apiKey = ""
}
