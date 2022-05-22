package it.redbyte.oauth2.exceptions

class InvalidTokenException(message: String?, cause: Throwable? = null) : OAuth2Exception(message, cause)