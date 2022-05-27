package ez.vertx.core

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.impl.ParsableMIMEValue

/**
 * - Merge queryParams and body into a json object.
 * - All params in query or form will be treated as strings.
 * - Supported content type: *&#47;json, *&#47;x-www-form-urlencoded (other content types will make the body ignored)
 */
fun RoutingContext.paramsAsJson(): JsonObject {
  val params = queryParams().toJson()
  val contentType = parsedHeaders().contentType() as ParsableMIMEValue
  contentType.forceParse()
  val bodyJson = when (contentType.subComponent()) {
    "json" -> body().asJsonObject()
    "x-www-form-urlencoded" -> request().formAttributes().toJson()
    else -> null
  }
  bodyJson?.let { params.mergeIn(it) }
  return params
}
