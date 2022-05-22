package it.redbyte.oauth2

import it.redbyte.oauth2.exceptions.InvalidTokenException
import org.springframework.security.core.userdetails.UserDetails

interface SessionTokenVerifier {
    /**
     * Function used to verify the state variable in the OAuth2 flow
     *
     * @param token is the token being verified, previously generate by a @see(it.redbyte.oauth2.SessionTokenGenerator)
     * @return The user retrived from the token, or null if there is no user embedded in the token
     * @throws InvalidTokenException when the token specified is invalid
     */
    @kotlin.jvm.Throws(InvalidTokenException::class)
    fun verifyToken(token: String): UserDetails?
}