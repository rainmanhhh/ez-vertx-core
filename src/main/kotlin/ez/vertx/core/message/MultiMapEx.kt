package ez.vertx.core.message

import io.vertx.core.MultiMap

private const val httpMethodKey = "httpMethod"

var MultiMap.httpMethod: String?
  get() = get(httpMethodKey)
  set(value) {
    if (value == null) remove(httpMethodKey)
    else set(httpMethodKey, value)
  }
