# RedSpringBoot - OAuth2 Client

### Installation
First, add the github repo for SpringLiveStomp in your pom.xml:
```xml
<repositories>
    <repository>
        <id>RedSpringBootOAuth2Client-mvn-repo</id>
        <url>https://github.com/Chiyo-no-sake/RedSpringBootOAuth2Client/raw/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

Now just define the dependency in you project pom.xml like the following:

```xml

<dependency>
    <groupId>it.redbyte</groupId>
    <artifactId>red-spring-boot-oauth2-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage

1) Define he following provider properties in application.yml

```yaml
...
oauth2:
  session-cookie:
    name: TSM_SESS_ID
  providers:
    linkedin:
      auth-url: "https://www.linkedin.com/oauth/v2/authorization"
      client-id: "---------"
      redirect-uri: "http://localhost:8080/api/login/oauth2/code/linkedin"
      scopes:
        - r_emailaddress
        - r_liteprofile
...
```

2) Define you provider classes, extending OAuth2Provider:

```kotlin
@Component
class LinkedinOAuth2Provider: OAuth2Provider {
    override val providerName = "linkedin"

    override fun callback(code: String): ResponseEntity<Any> {
        LoggerFactory.getLogger(this.javaClass).info("CALLBACK!!!!!")
        return ResponseEntity.ok(null)
    }
}
```

3) DONE!! Now the login endpoint should be exposed at ```{baseUrl}/oauth2/authentication/linkedin``` and the callback at ```/login/oauth2/code/linkedin```