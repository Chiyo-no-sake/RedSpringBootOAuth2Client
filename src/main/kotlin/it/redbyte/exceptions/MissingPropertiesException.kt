package it.redbyte.exceptions

class MissingPropertiesException(message: String?, cause: Throwable? = null): RuntimeException(message, cause) {
}