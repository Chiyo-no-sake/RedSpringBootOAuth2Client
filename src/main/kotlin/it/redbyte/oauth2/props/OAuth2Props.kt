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
        val clientSecret: String,
        val redirectUri: String,
        val tokenUrl: String,
        val scopes: List<String>,
    ) {
        companion object {
            // default value to use for not initialized non-null fields
            val defaultProviderProps: OAuth2ProviderProps = OAuth2ProviderProps(
                "localhost",
                "",
                "",
                "localhost",
                "localhost",
                arrayListOf()
            )
        }
    }

    @ConstructorBinding
    data class SessionCookieProps(
        val name: String
    )
}