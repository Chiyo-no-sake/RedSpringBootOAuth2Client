package it.redbyte.oauth2.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConstructorBinding
@ConfigurationProperties("oauth2")
data class OAuth2Props(
    @NestedConfigurationProperty val providers: Map<String, OAuth2ProviderProps>,
    @NestedConfigurationProperty val sessionCookie: SessionCookieProps
) {
    @ConstructorBinding
    data class OAuth2ProviderProps(
        val authUrl: String,
        val clientId: String,
        val redirectUri: String,
        val scopes: List<String>,
    )

    @ConstructorBinding
    data class SessionCookieProps(
        val name: String
    )
}