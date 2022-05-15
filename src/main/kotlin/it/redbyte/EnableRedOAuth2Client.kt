package it.redbyte

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@ComponentScan
@ConfigurationPropertiesScan
@EnableConfigurationProperties
annotation class EnableRedOAuth2Client()
