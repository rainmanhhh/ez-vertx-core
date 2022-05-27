package ez.vertx.core.busi

import ez.vertx.core.handler.CoroutineHandler
import ez.vertx.core.message.res.SimpleRes
import ez.vertx.core.message.res.check
import ez.vertx.core.message.sendMessage
import ez.vertx.core.paramsAsJson
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
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
    val address = ctx.normalizedPath()
    val httpMethod = ctx.request().method().name()
    val paramJson = ctx.paramsAsJson()
    logger.debug("req path: {}, httpMethod: {}, paramJson: {}", address, httpMethod, paramJson)
    val deliveryOptions = DeliveryOptions().addHeader("httpMethod", httpMethod)
    val resData = sendMessage(address, paramJson, SimpleRes::class.java, deliveryOptions).check()
    logger.debug("busiVerticle resData: {}", resData)
    ctx.response().putHeader(
      HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8"
    ).end(
      Json.encode(resData)
    ).await()
    return false
  }
}
