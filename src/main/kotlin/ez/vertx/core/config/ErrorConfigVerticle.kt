package ez.vertx.core.config

class ErrorConfigVerticle : ConfigVerticle<ErrorConfig>() {
  override val key = "error"
  override var configValue = ErrorConfig()
}

class ErrorConfig {
  var message = ErrorMessageConfig()
}

class ErrorMessageConfig {
  /**
   * required param not found in request
   */
  var paramRequired = "param required:"

  /**
   * param format error
   */
  var paramFormatError = "param format error:"

  /**
   * server-side apiKey config value is null
   */
  var serverApiKeyNotSet = "server api key not set"

  /**
   * apiKey in request is not match with server-side config value
   */
  var requestApiKeyIncorrect = "request api key incorrect"
}
