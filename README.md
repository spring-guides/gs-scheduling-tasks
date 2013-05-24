<!-- See expanded [macro:...] values at https://github.com/springframework-meta/springframework.org/tree/master/doc/gs-macros.md -->

# Getting Started: Scheduling Tasks

What you'll build
-----------------

This guide walks you through the steps for scheduling tasks with Spring.


What you'll need
----------------

 - About 15 minutes
 - [macro:prereq-editor-jdk-buildtools]

## [macro:how-to-complete-this-guide]


<a name="scratch"></a>
Set up the project
----------------------

[macro:build-system-intro]

[macro:create-directory-structure-hello]

### Create a Maven POM

[macro:maven-project-setup-options]

`pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-scheduling-tasks</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.bootstrap</groupId>
        <artifactId>spring-bootstrap-starters</artifactId>
        <version>0.5.0.BUILD-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.bootstrap</groupId>
            <artifactId>spring-bootstrap-starter</artifactId>
            <version>0.5.0.BUILD-SNAPSHOT</version>
        </dependency>
    </dependencies>
    
    <!-- TODO: remove once bootstrap goes GA -->
    <repositories>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>http://repo.springsource.org/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>http://repo.springsource.org/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
</project>
```

[macro:bootstrap-starter-pom-disclaimer]

<a name="initial"></a>
Create a scheduled task
-------------------------
Now that you've set up your project, you can create a scheduled task.


`src/main/java/hello/ScheduledTasks.java`

```java
package hello;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("The time is now " + dateFormat.format(new Date()));
    }
}
```

The key components that make this code perform scheduled tasks are the `@EnableScheduling` and `@Scheduled` annotations. 

`@EnableScheduling` ensures that a background task executor is created. Without it, nothing gets scheduled. 

You use `@Scheduled` to configure when a particular method is run.
> **Note:** This example uses `fixedRate`, which specifies the interval between method invocations measured from the start time of each invocation. There are [other options](http://static.springsource.org/spring/docs/3.2.2.RELEASE/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled), like `fixedDelay`, which specifies the interval between invocations measured from the completion of the task. You can also [use `@Scheduled(cron=". . .")` expressions for more sophisticated task scheduling](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html).

Make the application executable
-------------------------------

Although scheduled tasks can be embedded in web apps and WAR files, the simpler approach demonstrated below creates a standalone application. You package everything in a single, executable JAR file, driven by a good old Java `main()` method.

### Create a main class

Here you create a new `SpringApplication` and run it with the `ScheduledTasks` you defined earlier. This action creates a task executor and allows tasks to be scheduled."


`src/main/java/hello/Application.java`

```java
package hello;

import org.springframework.bootstrap.SpringApplication;

public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ScheduledTasks.class);
    }
}
```

### [macro:build-an-executable-jar]


Run the service
-------------------------------------

Run your service with `java -jar` at the command line:

    java -jar target/gs-scheduling-tasks-1.0-SNAPSHOT.jar

Logging output is displayed. You should see your scheduled task fire every 5 seconds:

    [...]
    The time is now 13:10:00
    The time is now 13:10:05
    The time is now 13:10:10
    The time is now 13:10:15

Summary
-------

Congratulations! You created an application with a scheduled task. Heck, the actual code was shorter than the build file! This technique works in any type of application.

[zip]: https://github.com/springframework-meta/gs-scheduling-tasks/archive/master.zip
