package it.redbyte.oauth2

import it.redbyte.oauth2.controllers.OAuth2AuthorizationController
import it.redbyte.oauth2.controllers.OAuth2CallbackController
import it.redbyte.oauth2.controllers.OAuth2ProviderInitializer
import it.redbyte.oauth2.props.OAuth2Props
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(OAuth2Props::class)
@ConditionalOnClass(UserDetails::class)
@Import(
    OAuth2ProviderInitializer::class,
    OAuth2AuthorizationController::class,
    OAuth2CallbackController::class,
)
open class OAuth2ClientAutoConfiguration {
    init {
        LoggerFactory.getLogger(this.javaClass).info("Red OAuth2 Client is Active")
    }

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
