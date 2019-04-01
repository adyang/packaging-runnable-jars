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

### Basic Runnable Jar With Dependencies
Sample dependency is located at lib/sample-dep.jar and is used by BasicAppWithDependency.java (to print a message to stdout).
1. Prepare manifest file by pointing the classpath to the dependency and stating main class as entry point:
    ```
    Manifest-Version: 1.0
    Class-Path: lib/sample-dep.jar
    Main-Class: BasicAppWithDependency
    ```
2. Compile app and package into jar using 'm' option to indicate manifest file
    ```bash
    javac BasicAppWithDependency.java -cp lib/sample-dep.jar
    jar cvfm basic-app-with-dep.jar manifest.txt BasicAppWithDependency.class
    java -jar basic-app-with-dep.jar
    ```
Should output the following:
```console
Running BasicAppWithDependency...
Running SampleDependency...
```
Note that for this method, the dependencies are external to the jar and have to be available on the path stated on the manifest.

### Shell Script With Embedded Runnable Jar
This is a hack to run java programs without needing to enter the `java -jar ...` command.

Reference: https://coderwall.com/p/ssuaxa/how-to-make-a-jar-file-linux-executable

```bash
cat jar-header.sh basic-app.jar > run-basic-app
chmod u+x run-basic-app
./run-basic-app
```
Output should be the same as the ["Basic Runnable Jar With No Dependencies"](#basic-runnable-jar-with-no-dependencies) example.

---

## Maven
Examples show how to use various plugins to generate an uberjar.
Change into `packaging-jar-maven` directory to run examples.

Reference: https://www.baeldung.com/executable-jar-with-maven

### Maven Assembly Plugin
1. Change into `maven-assembly` directory and examine the POM Plugin configuration. The predefined descriptor `jar-with-dependencies` is used, which will create a jar with all its dependencies included. Also the mainClass to use in the manifest is specified in the configuration.
    ```xml
    <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.1.1</version>
        <configuration>
            <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
            <archive>
                <manifest>
                    <mainClass>com.example.assembly.MavenApp</mainClass>
                </manifest>
            </archive>
        </configuration>
        <executions>
            <execution>
                <id>make-assembly</id>
                <phase>package</phase>
                <goals>
                    <goal>single</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```
2. In the module directory, package the jar and perform a test run.
    ```bash
    mvn package
    java -jar target/maven-assembly-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
Should output the following:
```console
(maven-assembly, MavenApp, Running)
```

Reference: http://maven.apache.org/plugins/maven-assembly-plugin/usage.html
