package it.redbyte.oauth2.exceptions

open class OAuth2Exception(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)