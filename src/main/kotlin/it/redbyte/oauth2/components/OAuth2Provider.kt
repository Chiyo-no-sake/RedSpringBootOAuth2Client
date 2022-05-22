package it.redbyte.oauth2.components

import it.redbyte.oauth2.props.OAuth2Props
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
abstract class OAuth2Provider {
    abstract val providerName: String

    // should never be null in client, assigned by OAuth2LoginController in startup
    var callbackController: it.redbyte.oauth2.controllers.OAuth2CallbackController? = null

    // should never be null in client, assigned by OAuth2ProviderController in startup
    var authorizationController: it.redbyte.oauth2.controllers.OAuth2AuthorizationController? = null

    // should never be default in client, assigned by OAuth2ProviderInitializer in startup
    var props: OAuth2Props.OAuth2ProviderProps =
        OAuth2Props.OAuth2ProviderProps.Companion.defaultProviderProps

    /**
     * This is the actual endpoint that receives callback request first.
     * It directly call the callback mapping of the loginController.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    fun handleCallback(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        callbackController?.handleCallbackMapping(this, httpServletRequest, httpServletResponse)
    }

    /**
     * This is the actual endpoint that receives auth request first.
     * It directly call the callback mapping og the providerController
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param user can be null, if no user is authenticated during request
     */
    fun handleAccessRequest(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        @AuthenticationPrincipal user: UserDetails?
    ) {
        authorizationController?.handleRequestAccess(this, httpServletRequest, httpServletResponse, user)
    }

    ///////////////////////////////////
    ///////// User Callbacks //////////
    ///////////////////////////////////

    /**
     * User defined error callback. Each provider must have a callback and an error callback that is called with error
     * details in case the user denies or cancel access.
     * This will be called by the OAuth2CallbackController.
     *
     * @param request
     * @param response
     */
    abstract fun errorCallback(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit

    /**
     * User defined callback. Each provider must have an error callback and a callback.
     * This will be called by the OAuth2AuthorizationController after having successfully retrived the token.
     *
     * @param token the access code used to make requests to provider
     * @param expiresIn time in seconds after which the token expires
     * @param user can be null. The user that requested the login. If it's null it should mean that the user is not yet
     * registered to the system. If it's not null, it is the user that owns the provider's access token.
     */
    abstract fun callback(
        token: String,
        expiresIn: Int,
        user: UserDetails?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Unit
}