# Pygmalion

This is a web mock CLI that can be used in order to create self-defined local endpoints for testing within seconds.

## Building and installing
- Clone this project.
- `cd pygmalion`.
- `gradlew build`.
- Add the `run` folder to your PATH.

### Recommendation: GraalVM
Running Pygmalion (and hence the unit tests as well) will require either the deprecated Nashorn engine or the Graaljs engine to be included in your Java Runtime Environment. This means that from JDK 15 onwards you will need to use [GraalVM](https://www.graalvm.org/) instead of the regular JDK for testing and running.

## Usage
```
pygmalion
> when get /hallo-welt then 'Hallo Welt'
```

Then open your browser and navigate to `http://localhost/hallo-welt`.

(Note that you can use single quotes for escaping.)

## RestTemplates
There are quite a lot of useful Pygmalion commands. One of the most powerful ones is the `restTemplate` command. Its syntax is:
```
> restTemplate /cookies --key=flavour
```
This will create the usual GET, POST, PUT, DELETE endpoints for /cookies.
It expects them to be json objects using the property specified in the argument as key (defaults to `id`).
Hence the following calls would work as expected after the command above:
```
POST http://localhost/cookies {"flavour":"chocolate"} -> 201 CREATED | "chocolate"
PUT http://localhost/cookies/chocolate {"flavour":"chocolate", "goesWith": "coffee"} -> 200 OK
GET http://localhost/cookies -> 200 OK | [{"flavour":"chocolate", "goesWith": "coffee"}]
DELETE http://localhost/cookies/chocolate -> 200 OK
GET http://localhost/cookies/chocolate -> 404 NOT FOUND
```
Note that the resources can even be initialized from a JSON array stored in a file:
```
> restTemplate /cookies from cookies.txt --key=flavour
```
If you add the `--persistent` flag, your data will be stored in a local database and hence still be present when restarting Pygmalion.

## Further examples

### Use another port
```
> port 8080
> when get /test then 'This is served on port 8080.'
```
Then check `http://localhost:8080/test`.

### Serve from file
```
> when get /test then from index.html
```

### Static serving
```
> static .
```
This will serve from the specified directory statically. (`.` is for current, you can use absolute path as well.) 

### Scripting
```
> apply script.txt
```
This will apply every line found in the provided file as if it had been entered in the CLI. (Lines starting with `#` or `//` are considered to be comments.)

### Echo request body
```
> when post /test then ${body}
```

### Echo body property
```
> when post /test then ${JSON.parse(body).name}
```

### Forward request
```
> when get / then forward https://google.com
```

### Status code
```
> when get /test then 'Works' status 200
```

### Dynamic evaluation
```
> when post /test then ${JSON.parse(queryParams).number + 3}
```
In general, expressions within `${}` are evaluated using the Java Scripting Extension (defaulting to Java Script), i.e. valid JavaScript expressions can be used.
It is also possible to configure the type of the engine itself via the `engine` command, e.g.:
```
engine jruby
```

The following properties are injected by Pygmalion via bindings and can be accessed from within the expressions:

|Property|Description|
|---|---|
|body|Request body|
|cookies|JSON string representing all cookies sent with the request|
|counter|How many times this endpoint has been called|
|headers|JSON string representing all request headers|
|queryParams|JSON string representing all queryParams|
|request|JSON string representing the whole request|
|timestamp|Current timestamp (ms since the epoch)|
|uri|The request's URI|
|restTemplates|JSON string representing the current state of all configured restTemplates (i.e. the resources themselves)|

The JavaScript expression can also be put into a file instead, like that:
```
> when post /test then eval test.js
```

## Websockets
It is possible to create websocket endpoints like this:
```
> websocket /test1
> websocket /test2
> websocket start
```
(Due to the underlying framework it is only possible to create websockets before every other request mapping.)

After setting up the endpoints messages can be sent to the websocket's clients:
```
> websocket /test
> websocket start
> websocket /test message 'Hello'
```
It is also possible to schedule recurring websocket messages like this:
```
> websocket /test recurring 'PING' 5000
```
This will send the message 'PING' to all clients connected to the `/test` websocket every 5 seconds.

Also, it is possible to configure a response to incoming messages:
```
> websocket /test respond ${message}
```
This will simply echo the incoming message.

## OpenAPI support
```
> openapi spec.yml
```
Assuming you have a json or yaml specification in OpenAPI format, you can use pygmalion to spawn a test server.

Also it is possible to export your current Pygmalion request configuration to OpenAPI:
```
> openapi-export exportFile.yaml
```
For resource templates Pygmalion will observe the entities you create and infer their schema for OpenAPI export.
## OAuth support
```
> oauth /oauth
```
You can use Pygmalion to spawn an OAuth2 test server. The above command creates routes `GET /oauth/authorize` and `POST /oauth/token` to allow for the Authorization Code Grant Flow (see https://tools.ietf.org/html/rfc6749#section-4.1).
The authorization endpoint simply redirects to the specified `redirect_uri` providing an (expiring) authorization code and the original `state`. The token endpoint validates the authorization code and issues JWTs that can be verified using the public key in `src/main/resources`. (Obviously, this key pair is not a secret at all..)

## Chaos Monkey
```
> chaosmonkey 42
```
This will fail about 42% of all requests with a status of 503.

## Documentation of all commands
```
pygmalion
> help
```

## Credits
This project is powered by the awesome Spark-Java project (i.e. its Kotlin version). Please see: https://github.com/perwendel/spark

## License
Copyright by Christopher Rudoll, 2018-2020. This is Free Software and published under the GPL 3.0 License.
