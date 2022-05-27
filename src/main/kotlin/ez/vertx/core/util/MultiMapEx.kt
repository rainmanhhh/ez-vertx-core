package ez.vertx.core

import io.vertx.core.MultiMap
import io.vertx.core.json.JsonObject

/**
 * All param values will be treated as string, so the result is a `Map<String, String>` json object.
 * Values in the result object will not be null or blank
 */
fun MultiMap.toJson() = JsonObject().also {
  for (entry in entries()) {
    if (!entry.value.isNullOrBlank()) {
      it.put(entry.key, entry.value)
    }
  }
}
