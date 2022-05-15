package it.redbyte.controllers

import it.redbyte.OAuth2Provider
import it.redbyte.SessionTokenGenerator
import it.redbyte.exceptions.MissingPropertiesException
import it.redbyte.props.OAuth2Props
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
class OAuth2ProviderController(
    val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    val oAuth2Props: OAuth2Props,
    val tokenService: SessionTokenGenerator,
    providers: List<OAuth2Provider>
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        providers.forEach {
            logger.info("Initializing provider authentication ${it.providerName}")

            val requestAccessMappingInfo = RequestMappingInfo
                .paths("oauth2/authorization/${it.providerName}")
                .methods(RequestMethod.GET)
                .build()

            val requestAccessMethod: Method = this.javaClass.getMethod(
                "requestAccess",
                HttpServletRequest::class.java,
                HttpServletResponse::class.java,
                UserDetails::class.java
            )

            requestMappingHandlerMapping.registerMapping(requestAccessMappingInfo, this, requestAccessMethod)
        }

    }

    fun requestAccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @AuthenticationPrincipal user: UserDetails?
    ) {
        val providerName = request.requestURI.split("/").last()
        logger.info("Requesting access to provider ${providerName}")

        val props = oAuth2Props.providers[providerName]
            ?: throw MissingPropertiesException("Missing provider in properties: $providerName")

        val clientId = props.clientId
        val redirectUri = props.redirectUri

        var scope = props.scopes
            .stream()
            .reduce("") { t, u -> "$t$u " }

        scope = scope.substring(0, scope.length - 1)

        val uri = "${props.authUrl}?" +
                "response_type=code&" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "scope=$scope"

        response.status = HttpStatus.FOUND.value()
        response.addHeader(HttpHeaders.LOCATION, uri)

        user?.let {
            val jwt = tokenService.createSessionToken(user)
            response.addCookie(Cookie(oAuth2Props.sessionCookie.name, jwt))
        }
    }
}