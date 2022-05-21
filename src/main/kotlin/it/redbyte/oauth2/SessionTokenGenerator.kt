package it.redbyte.oauth2

import org.springframework.security.core.userdetails.UserDetails

interface SessionTokenGenerator {
    fun createSessionToken(userDetails: UserDetails): String
}