# Pygmalion

This is a web mock CLI that can be used in order to create self-defined local endpoints for testing within seconds.

## Building and installing (for Windows)
- Clone this project.
- `cd pygmalion`.
- `gradlew clean build`.
- Add the `run` folder to your PATH.

(I`m pretty sure you Linux guys get the idea and are able to do this in a similar way on your machine as well.)

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

## Further examples

### Use another port
```
> port 8080
> when get /test then 'This is served on port 8080.'
```
Then check `http://localhost:8080/test`.
(Unfortunately, due to a limitation in the underlying framework it is currently not possible to mock APIs on multiple ports at once.)

### Serve from file
```
> when get /test then from index.html
```

### Scripting
```
> apply script.txt
```
This will apply every line found in the provided file as if it had been entered in the CLI.

### Echo request body
```
> when post /test then ${body}
```

### Echo body property
```
> when post /test then ${JSON.parse(body).name}
```

### Status code
```
> when get /test then 'Works' status 200
```

### Dynamic evaluation
```
> when post /test then ${JSON.parse(queryParams).number + 3}
```
In general, expressions within `${}` are evaluated using the Nashorn Project, i.e. valid JavaScript expressions can be used.

The following properties are injected by Pygmalion via bindings and can be accessed from within the expressions:

|Property|Description|
|---|---|
|body|Request body|
|counter|How many times this endpoint has been called|
|timestamp|Current timestamp (ms since the epoch)|
|headers|JSON string representing all request headers|
|queryParams|JSON string representing all queryParams|
|cookies|JSON string representing all cookies sent with the request|
|uri|The request's URI|

## OpenAPI support
```
> openapi spec.yml
```
Assuming you have a json or yaml specification in OpenAPI format, you can use pygmalion to spawn a test server. (This is still an incubating feature though.)

## OAuth support
```
> oauth /oauth
```
You can use Pygmalion to spawn an OAuth2 test server. The above command creates routes `GET /oauth/authorize` and `POST /oauth/token` to allow for the Authorization Code Grant Flow (see https://tools.ietf.org/html/rfc6749#section-4.1).
The authorization endpoint simply redirects to the specified `redirect_uri` providing an (expiring) authorization code and the original `state`. The token endpoint validates the authorization code and issues JWTs that can be verified using the public key in `src/main/resources`. (Obviously, this key pair is not a secret at all..)

## Documentation of all commands
```
pygmalion
> help
```

## Credits
This project is powered by the awesome Spark-Java project (i.e. its Kotlin version). Please see: https://github.com/perwendel/spark

## License
Copyright by Christopher Rudoll, 2018. This is Free Software and published under the GPL 3.0 License.