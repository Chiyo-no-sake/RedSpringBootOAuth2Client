package it.redbyte.oauth2.exceptions

class InvalidAuthCodeException(message: String?, cause: Throwable? = null) : OAuth2Exception(message, cause)