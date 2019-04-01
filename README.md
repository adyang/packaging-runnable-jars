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
Main dependency used is [VAVR](http://www.vavr.io/) and is declared in the parent POM. In particular, each example prints an io.vavr.Tuple of (EXAMPLE_NAME, MavenApp, Running) when run.
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

### Maven Shade Plugin
In addition to creating an uberjar, the Maven Shade plugin is able to rename the packages of dependencies, which is useful when there are unavoidable conflicts in using different versions of dependencies. (This is common in proprietary legacy libraries where they package their dependencies within the library instead of using a dependency management system.)
1. Change into `maven-shade` directory and examine the POM Plugin configuration. The `ManifestResourceTransformer` is used to specify the mainClass for the manifest. `createDependencyReducedPom` if set to true will create a new "reduced" POM file containing only dependencies that are excluded from the uberjar. Since in this example, we are not excluding any dependencies from the uberjar, this is set to false to remove clutter.  
    ```xml
    <plugin>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.2.1</version>
        <configuration>
            <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                    <mainClass>com.example.shade.MavenApp</mainClass>
                </transformer>
            </transformers>
            <createDependencyReducedPom>false</createDependencyReducedPom>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```
2. In the module directory, package the jar and perform a test run.
    ```bash
    mvn package
    java -jar target/maven-shade-1.0-SNAPSHOT.jar
    ```
Should output the following:
```console
(maven-shade, MavenApp, Running)
```

Reference: https://maven.apache.org/plugins/maven-shade-plugin/usage.html

### Spring Boot Maven Plugin
Usually used in Spring Boot projects but can also be used as a standalone plugin.
1. Change into `spring-boot-maven` directory and examine the POM Plugin configuration. The `mainClass` for the manifest can be specified directly in the configuration.  
    ```xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.1.3.RELEASE</version>
        <configuration>
            <mainClass>com.example.spring.boot.MavenApp</mainClass>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>repackage</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```
2. In the module directory, package the jar and perform a test run.
    ```bash
    mvn package
    java -jar target/spring-boot-maven-1.0-SNAPSHOT.jar
    ```
Should output the following:
```console
(spring-boot-maven, MavenApp, Running)
```

Reference: https://docs.spring.io/spring-boot/docs/current/maven-plugin/usage.html

---

## Gradle
Examples show how to use various tasks/ plugins to generate an uberjar.
Main dependency used is [VAVR](http://www.vavr.io/) and is declared in the root build script. In particular, each example prints an io.vavr.Tuple of (EXAMPLE_NAME, GradleApp, Running) when run.
Change into `packaging-jar-gradle` directory to run examples.

Reference: https://www.baeldung.com/gradle-fat-jar

### Java Plugin Jar Task
1. Examine the `build.gradle` configuration in the `java-jar-task` directory. The jar file will contain the class files from our main code `sourceSets.main.output` and also all the dependencies on the `runtimeClasspath`. The Main-Class is also specified as a manifest attribute in the configuration.
    ```gradle
    task uberJar(type: Jar) {
        archiveClassifier = 'uber'
        manifest {
            attributes 'Main-Class': 'com.example.jar.task.GradleApp'
        }

        from sourceSets.main.output

        dependsOn configurations.runtimeClasspath
        from {
            configurations.runtimeClasspath.findAll { it.name.endsWith('jar') }.collect { zipTree(it) }
        }
    }
    ```
2. Run the uberJar task under the `java-jar-task` subproject and perform a test run.
    ```bash
    ./gradlew :java-jar-task:uberJar
    java -jar java-jar-task/build/libs/java-jar-task-1.0-SNAPSHOT-uber.jar
    ```
Should output the following:
```console
(java-jar-task, GradleApp, Running)
```

References: https://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_packaging

### Shadow Plugin
1. Examine the `build.gradle` configuration in the `shadow` directory. The Shadow plugin will automatically include all class files from the main sourceSet and also from the runtime dependencies. It integrates with the application plugin such that the `mainClassName` specified will be reflected in the manifest.
    ```gradle
    plugins {
        id 'application'
        id 'com.github.johnrengelman.shadow' version '5.0.0'
    }

    mainClassName = 'com.example.shadow.GradleApp'
    ```
2. Run the shadowJar task under the `shadow` subproject and perform a test run.
    ```bash
    ./gradlew :shadow:shadowJar
    java -jar shadow/build/libs/shadow-1.0-SNAPSHOT-all.jar
    ```
Should output the following:
```console
(shadow, GradleApp, Running)
```

References:
- https://imperceptiblethoughts.com/shadow/getting-started/#default-java-groovy-tasks
- https://imperceptiblethoughts.com/shadow/application-plugin/#running-the-shadow-jar
