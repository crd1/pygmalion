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

## Examples

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

### Dynamic evaluation
```
> when post /test then ${JSON.parse(queryParams).number + 3}
```
In general, expressions within `${}` are evaluated using the Nashorn Project, i.e. valid JavaScript expressions can be used.

The following properties are injected by Pygmalion via bindings and can be accessed from within the expressions:
- body (Request body)
- counter (How many times this endpoint has been called)
- timestamp (Current timestamp, ms since the epoch)
- headers (JSON string representing all request headers)
- queryParams (JSON string representing all queryParams)
- cookies (JSON string representing all cookies sent with the request)
- uri (The request's URI)

## Documentation of all commands
```
pygmalion
> help
```

## License
Copyright by Christopher Rudoll, 2018. This is Free Software and published under the GPL 3.0 License.