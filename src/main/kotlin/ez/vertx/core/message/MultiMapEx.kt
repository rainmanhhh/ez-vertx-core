package ez.vertx.core.message

import io.vertx.core.MultiMap

private const val httpMethodKey = "httpMethod"

var MultiMap.httpMethod: String?
  get() = get(httpMethodKey)
  set(value) {
    if (value == null) remove(httpMethodKey)
    else set(httpMethodKey, value)
  }

private const val pathKey = "path"

var MultiMap.path: String?
  get() = get(pathKey)
  set(value) {
    if (value == null) remove(pathKey)
    else set(pathKey, value)
  }
