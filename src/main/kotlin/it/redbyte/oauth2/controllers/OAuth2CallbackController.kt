package it.redbyte.oauth2.controllers

import it.redbyte.oauth2.SessionTokenVerifier
import it.redbyte.oauth2.components.OAuth2Provider
import it.redbyte.oauth2.exceptions.InvalidAuthCodeException
import it.redbyte.oauth2.model.AccessTokenResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.*
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.jvm.javaType

@Controller
@Order(2)
class OAuth2CallbackController(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val sessionTokenVerifier: SessionTokenVerifier,
    private val rest: RestTemplate,
    private val providers: List<OAuth2Provider>
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun initializeProviders() {
        providers.forEach {
            logger.info("Initializing provider login ${it.providerName}")
            it.callbackController = this

            val loginMappingInfo = RequestMappingInfo
                .paths("login/oauth2/code/${it.providerName}")
                .methods(RequestMethod.GET)
                .build()

            val loginMethod: Method = it.javaClass
                .getMethod(
                    OAuth2Provider::handleCallback.name,
                    *OAuth2Provider::handleCallback.parameters.map {
                        Class.forName(it.type.javaType.typeName)
                    }.drop(1).toTypedArray()
                )
            requestMappingHandlerMapping.registerMapping(loginMappingInfo, it, loginMethod)
        }

    }

    /**
     * Handles the mapping of error/success user defined callbacks after provider authentication.
     * This function get called by each provider that implements OAuth2Provider.
     *
     * @param callingProvider is the provider that is requesting the callback
     * @param httpServletRequest the request to be handled
     * @param httpServletResponse the response to be returned
     */
    fun handleCallbackMapping(
        callingProvider: OAuth2Provider,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ) {
        // if state is not specified, we return error
        val state = if (!httpServletRequest.parameterMap.containsKey("state")) {
            httpServletResponse.status = HttpStatus.FORBIDDEN.value()
            httpServletResponse.writer.println("You don't have permission to use this callback")
            return
        } else {
            httpServletRequest.getParameter("state")
        }

        // verify the state
        val userDetails = sessionTokenVerifier.verifyToken(state)

        if (httpServletRequest.parameterMap.containsKey("code")) {

            // TODO:
            // Login to the provider here
            // and then call the callback function with
            // an optional UserDetails and the access token
            // callback

            val grantType = "authorization_code"
            val code = httpServletRequest.getParameter("code")
            val tokenUrl = callingProvider.props.tokenUrl
            val clientId = callingProvider.props.clientId
            val secret = callingProvider.props.clientSecret
            val redirectUri = callingProvider.props.redirectUri

            val body = LinkedMultiValueMap<String, String>().apply {
                this["code"] = code
                this["client_secret"] = secret
                this["client_id"] = clientId
                this["grant_type"] = grantType
                this["redirect_uri"] = redirectUri
            }

            val headers = LinkedMultiValueMap<String?, String?>().apply {
                this[HttpHeaders.CONTENT_TYPE] = listOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            }

            val request = HttpEntity(body, headers)

            val tokenResponse =
                try {
                    rest.exchange<AccessTokenResponse>(tokenUrl, HttpMethod.POST, request)
                } catch (ex: RestClientException) {
                    logger.warn(ex.stackTrace.toString())
                    throw InvalidAuthCodeException("Cannot login with provided auth code.", ex)
                }

            if (tokenResponse.statusCode != HttpStatus.OK) {
                throw InvalidAuthCodeException("Cannot login with provided auth code. Response status: ${tokenResponse.statusCode}\nBody: ${tokenResponse.body}")
            }

            val accessToken = tokenResponse.body.accessToken
            val expiresIn = tokenResponse.body.expiresIn

            callingProvider.callback(accessToken, expiresIn, userDetails, httpServletRequest, httpServletResponse)
        } else {
            callingProvider.errorCallback(httpServletRequest, httpServletResponse)
        }
    }
}