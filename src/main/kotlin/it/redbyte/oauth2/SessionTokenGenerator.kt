package it.redbyte.oauth2

import org.springframework.security.core.userdetails.UserDetails

interface SessionTokenGenerator {
    /**
     * Should create a token that has the ability to contain at least the information to identify the passed user,
     * such as a JWT. The token should also work without any user.
     * This token will later be parsed by @see(it.redbyte.SessionTokenVerifier)
     *
     * @param userDetails can be null or the user to identify in the token
     * @return the token
     */
    fun createSessionToken(userDetails: UserDetails? = null): String
}