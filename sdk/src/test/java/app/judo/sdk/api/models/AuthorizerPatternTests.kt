package app.judo.sdk.api.models

import app.judo.sdk.utils.shouldEqual
import org.junit.Test

class AuthorizerPatternTests {
    @Test
    fun `should exactly match pattern and host with a subdomain`() {
        var hit = false
        val authorizer = Authorizer(
            "api.example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://api.example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        true shouldEqual hit
    }

    @Test
    fun `should match wildcard`() {
        var hit = false
        val authorizer = Authorizer(
            "*.example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://api.example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        true shouldEqual hit
    }

    @Test
    fun `should not match differing subdomain`() {
        var hit = false
        val authorizer = Authorizer(
            "www.example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://api.example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        false shouldEqual hit
    }

    @Test
    fun `should not match a subdomain without a wildcard`() {
        var hit = false
        val authorizer = Authorizer(
            "example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://api.example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        false shouldEqual hit
    }

    @Test
    fun `should not match a TLD`() {
        var hit = false
        val authorizer = Authorizer(
            "com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        false shouldEqual hit
    }

    @Test
    fun `should not match a bare wildcard`() {
        var hit = false
        val authorizer = Authorizer(
            "*"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        false shouldEqual hit
    }

    @Test
    fun `should exactly match pattern and host`() {
        var hit = false
        val authorizer = Authorizer(
            "example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        true shouldEqual hit
    }

    @Test
    fun `should match a wildcard with no subdomain`() {
        var hit = false
        val authorizer = Authorizer(
            "*.example.com"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://example.com",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        true shouldEqual hit
    }

    @Test
    fun `should match nested subdomains with a wildcard`() {
        var hit = false
        val authorizer = Authorizer(
            "*.middleearth.net"
        ) {
            hit = true
        }
        val request = URLRequest(
            "https://elrond.rivendell.middleearth.net",
            HttpMethod.GET
        )
        authorizer.authorize(
            request
        )
        true shouldEqual hit
    }
}