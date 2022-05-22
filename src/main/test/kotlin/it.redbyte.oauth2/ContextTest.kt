package it.redbyte.oauth2

import it.redbyte.oauth2.props.OAuth2Props
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [OAuth2ClientAutoConfiguration::class])
@ActiveProfiles("test")
@EnableConfigurationProperties(OAuth2Props::class)
class ContextTest {

    @Test
    fun contextLoads() {
    }

}
