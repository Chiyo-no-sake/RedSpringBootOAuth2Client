package it.redbyte

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
interface OAuth2Provider {
    val providerName: String
    fun callback(code: String): ResponseEntity<Any>
}