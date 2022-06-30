package ez.vertx.core.busi

import ez.vertx.core.handler.CoroutineHandler
import ez.vertx.core.message.httpMethod
import ez.vertx.core.message.res.SimpleRes
import ez.vertx.core.message.res.check
import ez.vertx.core.message.sendMessage
import ez.vertx.core.paramsAsJson
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import org.slf4j.LoggerFactory

/**
 * send message to [ez.vertx.core.busi.BusiVerticle]
 */
class BusiHandler(scope: CoroutineScope) : CoroutineHandler(scope) {
  companion object {
    private val logger = LoggerFactory.getLogger(BusiHandler::class.java)
  }

  override suspend fun handleAsync(ctx: RoutingContext): Boolean {
    if (ctx.response().ended()) return false
    val httpMethod = ctx.request().method().name()
    var address = ctx.normalizedPath()
    var reqBody: Any = ctx.paramsAsJson()
    val deliveryOptions = DeliveryOptions().apply {
      headers.httpMethod = httpMethod
    }
    var res: SimpleRes<*>
    do {
      logger.debug("req path: {}, httpMethod: {}, reqBody: {}", address, httpMethod, reqBody)
      res = sendMessage(address, reqBody, SimpleRes::class.java, deliveryOptions)
      val resCode = res.code ?: 0
      if (resCode == HttpResponseStatus.CONTINUE.code()) {
        address = if (res.message.isNullOrBlank()) address else res.message
        val resData = res.data
        reqBody =
          when (resData) {
            null -> JsonObject()
            is JsonObject -> resData
            else -> JsonObject.mapFrom(resData)
          }
      }
    } while (resCode == HttpResponseStatus.CONTINUE.code())
    val resData = res.check()
    logger.debug("busiVerticle resData: {}", resData)
    ctx.response().putHeader(
      HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8"
    ).end(
      Json.encode(resData)
    ).await()
    return false
  }
}
