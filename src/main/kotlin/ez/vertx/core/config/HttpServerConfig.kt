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
   * you should pass this queryParam(name is `_apiKey`) when accessing admin resources
   */
  var apiKey = ""

  /**
   * common eventbus message address for handling busi requests.
   * - if it's not empty, all requests will be sent to this address
   * - otherwise [io.vertx.ext.web.RoutingContext.normalizedPath] will be used as address
   */
  var busiAddress = ""
}
