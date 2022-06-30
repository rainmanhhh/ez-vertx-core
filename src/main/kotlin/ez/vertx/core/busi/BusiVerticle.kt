package ez.vertx.core.busi

import ez.vertx.core.message.res.SimpleRes
import ez.vertx.core.err.HttpException
import ez.vertx.core.message.httpMethod
import ez.vertx.core.message.receiveMessage
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle

abstract class BusiVerticle<ResData> : CoroutineVerticle() {
  final override suspend fun start() {
    val p = path() ?: throw NullPointerException(javaClass.name + ".path should not return null")
    if (p == "/" || p.startsWith("/_admin/"))
      throw IllegalArgumentException(
        javaClass.name + " should not use `/` or `/_admin/**/*` path which are reserved by system handlers"
      )
    receiveMessage(p) {
      val httpMethod = it.headers.httpMethod?.let(HttpMethod::valueOf)
      serveAsync(httpMethod, it.body)
    }
  }

  /**
   * http request path(start with `/`). eg: `/getUserList`.
   * will be used as eventbus message address too. (when [HttpServerVerticle] received a request, it will send a message with this address)
   */
  abstract fun path(): String?

  /**
   * async version of [serve]
   */
  open suspend fun serveAsync(httpMethod: HttpMethod?, params: JsonObject): ResData = serve(httpMethod, params)

  /**
   * handle the http request.
   * if returned [SimpleRes.code] is [HttpResponseStatus.CONTINUE],
   * the response will be sent to "next" verticle:
   * [SimpleRes.message] will be used as eventbus message address;
   * [SimpleRes.data] will be used as request param object(so it should be a [JsonObject],
   * or an object which could be mapped into [JsonObject]).
   *
   * **examples**:
   * - sending `{a: 1}` to address "foo.bar"(you should register another verticle to handle it)
   * ```kotlin
   * return SimpleRes.continueTo("foo.bar", mapOf("a" to 1))
   * ```
   * - write `[1, 2, 3]` to client
   * ```kotlin
   * return arrayOf(1, 2, 3)
   * ```
   * - report 403 error to client
   * ```kotlin
   * throw ez.vertx.core.err.HttpException.forbidden("access denied")
   * ```
   *
   * @param params merge url query params with request body(support json or form data)
   * @return a [SimpleRes] object, or [SimpleRes.data].
   */
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
