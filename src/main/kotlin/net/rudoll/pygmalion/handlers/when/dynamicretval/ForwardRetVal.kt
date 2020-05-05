package net.rudoll.pygmalion.handlers.`when`.dynamicretval


import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpRequest
import spark.Request
import spark.Response
import java.io.BufferedReader
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import javax.servlet.http.HttpServletRequest

class ForwardRetVal(targetHostString: String) : DynamicRetVal {
    private val targetHost: HttpHost

    init {
        val url = URL(targetHostString)
        targetHost = HttpHost(url.host, url.port, url.protocol)
    }

    override fun getRetVal(request: Request, response: Response): String {
        if (request is DynamicRetVal.DummyRequest) {
            return "" // we cannot produce an example value without a request..
        }
        val forwardedResponse = forwardRequest(request.requestMethod(), targetHost, request.raw(), request.headers().map { BasicHeader(it, request.headers(it)) }.toTypedArray())
        response.status(forwardedResponse.statusLine.statusCode)
        return forwardedResponse.entity.content.bufferedReader().use(BufferedReader::readText)
    }

    private fun forwardRequest(method: String, targetHost: HttpHost, req: HttpServletRequest, headers: Array<Header>): CloseableHttpResponse {
        val targetUri = req.requestURI + getQueryString(req)
        val httpRequest = when (method.toUpperCase()) {
            "POST" -> {
                HttpPost(targetUri).withEntity(req)
            }
            "PUT" -> {
                HttpPut(targetUri).withEntity(req)
            }
            else -> {
                BasicHttpRequest(method, targetUri)
            }
        }
        httpRequest.setHeaders(headers)
        return httpClient.execute(targetHost, httpRequest)
    }

    private fun HttpEntityEnclosingRequestBase.withEntity(req: HttpServletRequest): HttpRequest {
        this.entity = InputStreamEntity(req.inputStream, req.contentLength.toLong())
        return this
    }

    private fun getQueryString(request: HttpServletRequest): String {
        if (request.queryString == null) {
            return ""
        }
        val queryStringComponents = request.queryString.split("&").map {
            return if (it.contains("=")) {
                URI(null, null, null, URLDecoder.decode(it.split("=".toRegex(), 2).last(), "UTF-8"), null).toString().substring(1).replace("&", "%26")
            } else {
                URI(null, null, null, URLDecoder.decode(it, "UTF-8"), null).toString().substring(1)
            }
        }
        return "?" + queryStringComponents.joinToString(separator = "&")
    }

    companion object {
        private val httpClient = HttpClientBuilder.create().build()
    }
}

