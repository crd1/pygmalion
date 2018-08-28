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
(Unfortunately, due to a limitation in the underlying framework it is currently not possible to mock APIs on multiple ports at once.)

### Serve from file
```
> when get /test then from index.html
```

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
> when post /test then ${JSON.parse(body).number + 3}
```
(In general, expressions within `${}` are evaluated using the Nashorn Project, i.e. valid JavaScript expressions can be used. The `body` property in the example is injected by Pygmalion via bindings.)

## Documentation of all commands
```
pygmalion
> help
```

## License
Copyright by Christopher Rudoll, 2018. This is Free Software and published under the GPL 3.0 License.