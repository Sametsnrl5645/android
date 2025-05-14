package io.homeassistant.companion.android.util

import android.net.Uri
import io.homeassistant.companion.android.common.data.MalformedHttpUrlException
import io.homeassistant.companion.android.common.data.authentication.impl.AuthenticationService
import java.net.URI
import java.net.URL
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import timber.log.Timber

object UrlUtil {
    fun formattedUrlString(url: String): String {
        return if (url == "") {
            throw MalformedHttpUrlException()
        } else {
            try {
                val httpUrl = url.toHttpUrl()
                HttpUrl.Builder()
                    .scheme(httpUrl.scheme)
                    .host(httpUrl.host)
                    .port(httpUrl.port)
                    .toString()
            } catch (e: IllegalArgumentException) {
                throw MalformedHttpUrlException(
                    e.message,
                )
            }
        }
    }

    fun buildAuthenticationUrl(url: String): String {
        return url.toHttpUrlOrNull()!!
            .newBuilder()
            .addPathSegments("auth/authorize")
            .addEncodedQueryParameter("response_type", "code")
            .addEncodedQueryParameter("client_id", AuthenticationService.CLIENT_ID)
            .build()
            .toString()
    }

    fun handle(base: URL?, input: String): URL? {
        val asURI = try {
            URI(input.removePrefix("homeassistant://navigate/"))
        } catch (e: Exception) {
            Timber.w(e, "Invalid input, returning base only")
            null
        }
        return when {
            asURI == null -> base
            isAbsoluteUrl(input) -> asURI.toURL()
            else -> { // Input is relative to base URL
                buildRelativeUrl(base, asURI)
            }
        }
    }

    private fun buildRelativeUrl(base: URL?, inputURI: URI): URL? {
        val builder = base?.toHttpUrlOrNull()?.newBuilder() ?: return null
        inputURI.path?.trim()?.takeIf { it.isNotBlank() }?.removePrefix("/")?.let { path ->
            // If the URL contains a `,`
            val beforeComma = path.substringBefore(",")
            val afterComma = path.substringAfter(",", "")
            builder.addPathSegments(beforeComma)
            if (afterComma.isNotEmpty()) {
                builder.addEncodedPathSegment(Uri.encode(",$afterComma"))
            }
        }

        inputURI.query?.trim()?.takeIf { it.isNotBlank() }?.let(builder::query)
        inputURI.fragment?.trim()?.takeIf { it.isNotBlank() }?.let(builder::fragment)

        return builder.build().toUrl()
    }

    fun isAbsoluteUrl(it: String?): Boolean {
        return Regex("^https?://").containsMatchIn(it.toString())
    }

    /** @return `true` if both URLs have the same 'base': an equal protocol, host, port and userinfo */
    fun URL.baseIsEqual(other: URL?): Boolean =
        if (other == null) {
            false
        } else {
            host?.lowercase() == other.host?.lowercase() &&
                port.let { if (it == -1) defaultPort else it } == other.port.let { if (it == -1) defaultPort else it } &&
                protocol?.lowercase() == other.protocol?.lowercase() &&
                userInfo == other.userInfo
        }

    fun splitNfcTagId(it: Uri?): String? {
        val matches =
            Regex("^https?://www\\.home-assistant\\.io/tag/(.*)").find(
                it.toString(),
            )
        return matches?.groups?.get(1)?.value
    }
}
