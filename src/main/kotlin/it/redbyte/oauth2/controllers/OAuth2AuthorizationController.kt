package it.redbyte.oauth2.controllers

import it.redbyte.oauth2.SessionTokenGenerator
import it.redbyte.oauth2.components.OAuth2Provider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.jvm.javaType


@Controller
@Order(3)
class OAuth2AuthorizationController(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val tokenService: SessionTokenGenerator,
    private val providers: List<OAuth2Provider>
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun initializeProviders(){
        providers.forEach {
            logger.info("Initializing provider authentication ${it.providerName}")
            it.authorizationController = this

            val requestAccessMappingInfo = RequestMappingInfo
                .paths("oauth2/authorization/${it.providerName}")
                .methods(RequestMethod.GET)
                .build()

            val requestAccessMethod: Method = it.javaClass.getMethod(
                OAuth2Provider::handleAccessRequest.name,
                *OAuth2Provider::handleAccessRequest.parameters.map {
                    Class.forName(it.type.javaType.typeName)
                }.drop(1).toTypedArray()
            )

            requestMappingHandlerMapping.registerMapping(requestAccessMappingInfo, it, requestAccessMethod)
        }
    }

    fun handleRequestAccess(
        provider: OAuth2Provider,
        request: HttpServletRequest,
        response: HttpServletResponse,
        @AuthenticationPrincipal user: UserDetails?
    ) {
        val providerName = provider.providerName
        logger.info("Requesting access to provider ${providerName}")

        val props = provider.props
        val clientId = props.clientId
        val redirectUri = props.redirectUri

        var scope = props.scopes
            .stream()
            .reduce("") { t, u -> "$t$u " }

        scope = scope.substring(0, scope.length - 1)

        var uri = "${props.authUrl}?" +
                "response_type=code&" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "scope=$scope"

        val jwt =
            if (user == null)
                tokenService.createSessionToken()
            else
                tokenService.createSessionToken(user)

        uri += "&state=$jwt"

        response.status = HttpStatus.FOUND.value()
        response.addHeader(HttpHeaders.LOCATION, uri)
    }
}