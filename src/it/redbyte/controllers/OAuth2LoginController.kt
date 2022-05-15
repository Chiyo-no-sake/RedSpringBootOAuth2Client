package it.redbyte.controllers

import it.redbyte.OAuth2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

@Controller
class OAuth2PropsOAuth2LoginController(
    val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    providers: List<OAuth2Provider>
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    init {
        providers.forEach {
            logger.info("Initializing provider login ${it.providerName}")

            val loginMappingInfo = RequestMappingInfo
                .paths("login/oauth2/code/${it.providerName}")
                .methods(RequestMethod.GET)
                .params("code")
                .build()

            val loginMethod: Method = it.javaClass.getMethod("callback", String::class.java)
            requestMappingHandlerMapping.registerMapping(loginMappingInfo, it, loginMethod)
        }

    }

}