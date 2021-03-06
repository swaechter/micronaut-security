package io.micronaut.security.token.jwt.cookie

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification

class JwtCookieSameSiteSpec extends Specification {

    void "test same-site is set from jwt cookie settings"() {
        ApplicationContext context = ApplicationContext.run(
                [
                        'spec.name': 'jwtcookie',
                        'micronaut.http.client.followRedirects': false,
                        'micronaut.security.enabled': true,
                        'micronaut.security.endpoints.login.enabled': true,
                        'micronaut.security.endpoints.logout.enabled': true,
                        'micronaut.security.token.jwt.enabled': true,
                        'micronaut.security.token.jwt.bearer.enabled': false,
                        'micronaut.security.token.jwt.cookie.enabled': true,
                        'micronaut.security.token.jwt.cookie.cookie-max-age': '5m',
                        'micronaut.security.token.jwt.cookie.cookie-same-site': 'None',
                        'micronaut.security.token.jwt.cookie.login-failure-target-url': '/login/authFailed',
                        'micronaut.security.token.jwt.signatures.secret.generator.secret': 'qrD6h8K6S9503Q06Y6Rfk21TErImPYqa',
                ], Environment.TEST)

        EmbeddedServer embeddedServer = context.getBean(EmbeddedServer).start()

        RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

        HttpRequest loginRequest = HttpRequest.POST('/login', new LoginForm(username: 'sherlock', password: 'password'))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)

        HttpResponse loginRsp = client.toBlocking().exchange(loginRequest, String)

        when:
        String cookie = loginRsp.getHeaders().get('Set-Cookie')

        then:
        cookie.contains('SameSite=None')

        cleanup:
        context.close()
    }

    void "test same-site cookie setting cannot have invalid value"() {
        ApplicationContext context = ApplicationContext.run(
                [
                        'spec.name': 'jwtcookie',
                        'micronaut.http.client.followRedirects': false,
                        'micronaut.security.enabled': true,
                        'micronaut.security.endpoints.login.enabled': true,
                        'micronaut.security.endpoints.logout.enabled': true,
                        'micronaut.security.token.jwt.enabled': true,
                        'micronaut.security.token.jwt.bearer.enabled': false,
                        'micronaut.security.token.jwt.cookie.enabled': true,
                        'micronaut.security.token.jwt.cookie.cookie-max-age': '5m',
                        'micronaut.security.token.jwt.cookie.cookie-same-site': 'nonesense',
                        'micronaut.security.token.jwt.cookie.login-failure-target-url': '/login/authFailed',
                        'micronaut.security.token.jwt.signatures.secret.generator.secret': 'qrD6h8K6S9503Q06Y6Rfk21TErImPYqa',
                ], Environment.TEST)

        EmbeddedServer embeddedServer = context.getBean(EmbeddedServer).start()

        RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

        HttpRequest loginRequest = HttpRequest.POST('/login', new LoginForm(username: 'sherlock', password: 'password'))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)

        HttpResponse loginRsp = client.toBlocking().exchange(loginRequest, String)

        when:
        String cookie = loginRsp.getHeaders().get('Set-Cookie')

        then:
        cookie.contains('SameSite') == false

        cleanup:
        context.close()
    }

    void "test same-site has no default"() {
        ApplicationContext context = ApplicationContext.run(
                [
                        'spec.name': 'jwtcookie',
                        'micronaut.http.client.followRedirects': false,
                        'micronaut.security.enabled': true,
                        'micronaut.security.endpoints.login.enabled': true,
                        'micronaut.security.endpoints.logout.enabled': true,
                        'micronaut.security.token.jwt.enabled': true,
                        'micronaut.security.token.jwt.bearer.enabled': false,
                        'micronaut.security.token.jwt.cookie.enabled': true,
                        'micronaut.security.token.jwt.cookie.cookie-max-age': '5m',
                        'micronaut.security.token.jwt.cookie.login-failure-target-url': '/login/authFailed',
                        'micronaut.security.token.jwt.signatures.secret.generator.secret': 'qrD6h8K6S9503Q06Y6Rfk21TErImPYqa',
                ], Environment.TEST)

        EmbeddedServer embeddedServer = context.getBean(EmbeddedServer).start()

        RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

        HttpRequest loginRequest = HttpRequest.POST('/login', new LoginForm(username: 'sherlock', password: 'password'))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)

        HttpResponse loginRsp = client.toBlocking().exchange(loginRequest, String)

        when:
        String cookie = loginRsp.getHeaders().get('Set-Cookie')

        then:
        cookie.contains('SameSite') == false

        cleanup:
        context.close()
    }
}
