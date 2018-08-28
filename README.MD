# Pygmalion

This is a web mock CLI that can be used in order to create self-defined local endpoints for testing within seconds.

## Building and installing

- Issue `gradlew clean build`.
- Add the `run` folder to your PATH.

## Usage
```
pygmalion
> when get /hallo-welt then 'Hallo Welt'
```

Then open your browser and navigate to `http://localhost/hallo-welt`.

## Documentation
```
pygmalion
> help
```

## License
This is Free Software and published under the Apache 2.0 License.