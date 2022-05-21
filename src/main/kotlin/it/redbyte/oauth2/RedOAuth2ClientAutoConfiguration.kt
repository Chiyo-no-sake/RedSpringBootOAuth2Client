package it.redbyte.oauth2

import it.redbyte.oauth2.props.OAuth2Props
import it.redbyte.oauth2.controllers.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@Import(OAuth2LoginController::class, OAuth2ProviderController::class)
@EnableConfigurationProperties(OAuth2Props::class)
@ConditionalOnClass(UserDetails::class)
open class OAuth2ClientAutoConfiguration() {
    init {
        LoggerFactory.getLogger(this.javaClass).info("Red OAuth2 Client is Active")
    }
}
