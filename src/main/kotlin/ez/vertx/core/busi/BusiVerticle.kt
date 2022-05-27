package ez.vertx.core.busi

import ez.vertx.core.err.HttpException
import ez.vertx.core.message.receiveMessage
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle

abstract class BusiVerticle<ResData> : CoroutineVerticle() {
  override suspend fun start() {
    val p = path() ?: throw NullPointerException(javaClass.name + ".path should not return null")
    if (p == "/" || p.startsWith("/_admin/"))
      throw IllegalArgumentException(
        javaClass.name + " should not use `/` or `/_admin/**/*` path which are reserved by system handlers"
      )
    receiveMessage(p) {
      val httpMethod = it.headers["httpMethod"]?.let(HttpMethod::valueOf)
      serve(httpMethod, it.body)
    }
  }

  /**
   * should return the request path(start with `/`). eg: `/getUserList`
   */
  abstract fun path(): String?

  open fun serve(httpMethod: HttpMethod?, params: JsonObject): ResData =
    when (httpMethod) {
      HttpMethod.GET -> get(params)
      HttpMethod.POST -> post(params)
      HttpMethod.DELETE -> delete(params)
      HttpMethod.PUT -> put(params)
      HttpMethod.PATCH -> patch(params)
      else -> throw HttpException.methodNotAllowed(httpMethod)
    }

  /**
   * like [serve] but only deal with [HttpMethod.GET]
   */
  open fun get(params: JsonObject): ResData = throw HttpException.methodNotAllowed(HttpMethod.GET)

  /**
   * like [serve] but only deal with [HttpMethod.POST]
   */
  open fun post(params: JsonObject): ResData = throw HttpException.methodNotAllowed(HttpMethod.POST)

  /**
   * like [serve] but only deal with [HttpMethod.DELETE]
   */
  open fun delete(params: JsonObject): ResData =
    throw HttpException.methodNotAllowed(HttpMethod.DELETE)

  /**
   * like [serve] but only deal with [HttpMethod.PUT]
   */
  open fun put(params: JsonObject): ResData = throw HttpException.methodNotAllowed(HttpMethod.PUT)

  /**
   * like [serve] but only deal with [HttpMethod.PATCH]
   */
  open fun patch(params: JsonObject): ResData =
    throw HttpException.methodNotAllowed(HttpMethod.PATCH)
}
