# Packaging Runnable Jars

## Basic Commandline
Change into the `basic` directory to run examples.

### Basic Runnable Jar With No Dependencies
1. Compile java program and package into jar using 'e' option to state entry main class  (BasicApp)
    ```bash
    javac BasicApp.java
    jar cvfe basic-app.jar BasicApp BasicApp.class
    java -jar basic-app.jar
    ```
Should output the following:
```console
Running BasicApp...
```
