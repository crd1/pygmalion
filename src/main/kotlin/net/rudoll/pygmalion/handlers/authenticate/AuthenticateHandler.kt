package net.rudoll.pygmalion.handlers.authenticate

import net.rudoll.pygmalion.common.PortManager
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark.before
import spark.Spark.halt
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

object AuthenticateHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

    override fun getDocumentation(): String {
        return "authenticate \$route [with] \$username \$password"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "authenticate"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        val route = input.second()
        val usernameIndex = if (input.third() == "with") 3 else 2 // "with" is optional
        val username = input.getTokens()[usernameIndex]
        val password = input.getTokens()[usernameIndex + 1]
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                PortManager.ensurePortIsSet(parsedInput)
                before(BasicAuthFilter(route, username, password))
            }
        })
        input.consume(usernameIndex + 2)
    }

    class BasicAuthFilter(private val route: String, private val username: String, private val password: String) : Filter {
        override fun handle(request: Request, response: Response) {
            if (!request.pathInfo().startsWith(route)) {
                return
            }
            try {
                val basicAuthHeader = request.headers("Authorization")
                val encodedCredentials = basicAuthHeader.replaceFirst("Basic ", "")
                val decodedCredentials = String(Base64.getDecoder().decode(encodedCredentials), UTF_8).split(":")
                val authenticated = decodedCredentials[0] == username && decodedCredentials[1] == password
                if (authenticated) {
                    return
                }
            } catch (e: Exception) {
                // simply not authenticated
            }
            response.header("WWW-Authenticate", "Basic")
            halt(401)
        }
    }
}
