package it.redbyte.oauth2.controllers

import it.redbyte.oauth2.components.OAuth2Provider
import it.redbyte.oauth2.exceptions.MissingPropertiesException
import it.redbyte.oauth2.props.OAuth2Props
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
@Order(1)
class OAuth2ProviderInitializer(
    private val oAuth2Props: OAuth2Props,
    private val providers: List<OAuth2Provider>
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun initializeProviders() {
        this.providers.forEach {
            logger.info("Initializing provider ${it.providerName}")

            it.props = oAuth2Props.providers[it.providerName]
                ?: throw MissingPropertiesException("Missing properties for provider ${it.providerName}")
        }
    }
}