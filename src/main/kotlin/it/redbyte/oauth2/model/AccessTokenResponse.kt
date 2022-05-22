package it.redbyte.oauth2.model

import com.fasterxml.jackson.annotation.JsonAlias

data class AccessTokenResponse(
    @JsonAlias("access_token", "access-token", "token")
    val accessToken: String,

    @JsonAlias("expires_in", "expires-in")
    val expiresIn: Int,
)