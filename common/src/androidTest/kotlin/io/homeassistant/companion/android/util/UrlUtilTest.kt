package io.homeassistant.companion.android.util

import java.net.URL
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UrlUtilTest {

    @Test
    fun given_valid_URL_and_input_with_comma_when_invoking_handle_then_return_valid_URL() {
        assertEquals(
            URL("http://127.0.0.1:8123/media-browser/browser/app/%2Cmedia-source%3A%2F%2Fmedia_source"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://navigate/media-browser/browser/app,media-source://media_source"),
        )

        assertEquals(
            URL("http://127.0.0.1:8123/media-browser/browser/app/%2Cmedia-source%3A%2F%2Fmedia_source"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://navigate/media-browser/browser/app%2Cmedia-source%3A%2F%2Fmedia_source"),
        )

        assertEquals(
            URL("http://127.0.0.1:8123/media-browser/browser/app/%2Cmedia-source%3A%2F%2Fmedia_source%2Cmedia-source%3A%2F%2Fmedia_source"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://navigate/media-browser/browser/app%2Cmedia-source%3A%2F%2Fmedia_source%2Cmedia-source%3A%2F%2Fmedia_source"),
        )
    }

    @Test
    fun given_valid_URL_and_input_with_query_when_invoking_handle_then_return_valid_URL() {
        assertEquals(
            URL("http://127.0.0.1:8123/?hello=world"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://?hello=world"),
        )

        assertEquals(
            URL("http://127.0.0.1:8123/media-browser/browser/app/%2Cmedia-source%3A%2F%2Fmedia_source?hello=world"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://navigate/media-browser/browser/app,media-source://media_source?hello=world"),
        )
    }

    @Test
    fun given_valid_URL_and_input_with_fragment_when_invoking_handle_then_return_valid_URL() {
        assertEquals(
            URL("http://127.0.0.1:8123/#hello-world"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://#hello-world"),
        )

        assertEquals(
            URL("http://127.0.0.1:8123/media-browser/browser/app/%2Cmedia-source%3A%2F%2Fmedia_source#hello-world"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "homeassistant://navigate/media-browser/browser/app,media-source://media_source#hello-world"),
        )
    }

    @Test
    fun given_valid_URL_and_no_input_when_invoking_handle_then_return_valid_URL() {
        assertEquals(
            URL("http://127.0.0.1:8123/"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), ""),
        )
    }

    @Test
    fun given_valid_URL_and_absolute_input_when_invoking_handle_then_return_valid_URL() {
        assertEquals(
            URL("http://127.0.0.1:8123/value"),
            UrlUtil.handle(URL("http://127.0.0.1:8123"), "http://127.0.0.1:8123/value"),
        )
    }
}
