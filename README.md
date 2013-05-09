# Getting Started with Scheduling Tasks

Introduction
------------

### What You'll Build

This guide will walk you through the steps needed to scheduled some tasks using Spring.

### What You'll Need

 - About 15 minutes
 - A favorite text editor or IDE
 - [JDK 7](http://docs.oracle.com/javase/7/docs/webnotes/install/index.html) or better
 - Your choice of Maven (3.0+) or Gradle (1.5+)

### How to Complete this Guide

Like all Spring's [Getting Started guides](/getting-started), you can choose to start from scratch and complete each step, or you can jump past basic setup steps that may already be familiar to you. Either way, you'll end up with working code.

To **start from scratch**, just move on to the next section and start [setting up the project](#scratch).

If you'd like to **skip the basics**, then do the following:

 - [download][zip] and unzip the source repository for this guide—or clone it using [git](/understanding/git):
`git clone https://github.com/springframework-meta/gs-scheduling-tasks.git`
 - cd into `gs-rest-service/initial`
 - jump ahead to [creating a representation class](#initial).

And **when you're finished**, you can check your results against the the code in `gs-scheduling-tasks/complete`.

<a name="scratch"></a>
Setting up the project
----------------------
First you'll need to set up a basic build script. You can use any build system you like when building apps with Spring, but we've included what you'll need to work with [Maven](https://maven.apache.org) and [Gradle](http://gradle.org) here. If you're not familiar with either of these, you can refer to our [Getting Started with Maven](../gs-maven/README.md) or [Getting Started with Gradle](../gs-gradle/README.md) guides.

### Maven

Create a Maven POM that looks like this:

`pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-scheduling-tasks</artifactId>
    <version>0.0.1-SNAPSHOT</version>

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
TODO: mention that we're using Spring Bootstrap's [_starter POMs_](../gs-bootstrap-starter) here.

Experienced Maven users who feel nervous about using an external parent project: don't panic, you can take it out later, it's just there to reduce the amount of code you have to write to get started.

### Gradle

TODO: paste complete build.gradle.

Add the following within the `dependencies { }` section of your build.gradle file:

`build.gradle`
```groovy
compile "org.springframework.bootstrap:spring-bootstrap-starter:0.5.0.BUILD-SNAPSHOT"
```

<a name="initial"></a>

Creating a Scheduled Task
-------------------------
With our build system in place, let's create a scheduled task.


`src/main/java/hello/ScheduledTasks.java`

```java
package hello;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {
	
	@Scheduled(fixedRate=5000)
    public void greetingsFromScheduledSpring() {
		System.out.println("Greetings from a Spring-scheduled task!");
    }
}
```

By tagging this class with `@Component`, it can be picked up when we create a Spring application context.

The key component to making it perform scheduled tasks is the `@Scheduled` annotation applied to our method. In this code block we have it configured to run the method every five seconds, regardless of how long the method takes to run.

The example above uses `fixedRate`. 
- `@Scheduled(fixedRate=<milliseconds>)` measures the time interval by starting at the beginning of the task. 
- `@Scheduled(fixedDelay=<milliseconds>)` measures the time interval by starting at the end of the task.

Imagine the task is very long running, perhaps taking ten minutes to run. If the scheduled task was configured with a `fixedRate` of ten seconds, multiple instances would be launched and inevitably consume too many resources. But if it was configured with a `fixedDelay` of ten seconds instead, one instance would run to completion before scheduling the next task.

It's also possible to [schedule things using `@Scheduled(cron=". . .")` expressions](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) for more sophisticated scheduling.

We also need `@EnableScheduling` so that our task will be added to Spring's default task executor.

Creating an executable main class
---------------------------------

The only left to do is create a runnable class!

`src/main/java/hello/Application.java`

```java
package hello;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

	public static void main(String[] args) throws Exception {
		new AnnotationConfigApplicationContext(ScheduledTasks.class);
	}
	
}
```

We are creating a new Spring application context and feeding the class with our scheduled task. This will cause a task executor thread to start up and begin processing automatically scheduled tasks until we terminate the process.


Building an executable JAR
--------------------------

Add the following to your `pom.xml` file (keeping any existing properties or plugins intact):

`pom.xml`
```xml
<properties>
	<start-class>hello.Application</start-class>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

The following will produce a single executable JAR file containing all necessary dependency classes:

```
$ mvn package
```

Running the Service
-------------------------------------

Now you can run it from the jar as well, and distribute that as an executable artifact:

```
$ java -jar target/gs-scheduling-tasks-0.0.1-SNAPSHOT.jar

... starts printing "Hello" every five seconds ...

```

Congratulations! You have created an application with scheduled tasks. Heck, the actual code was shorter than the build file! Suffice it to say, this technique works inside any type of application, web or command-line.

Related Resources
-----------------

* [Spring Framework 3.2.2.RELEASE official docs for scheduling tasks](http://static.springsource.org/spring/docs/3.2.2.RELEASE/spring-framework-reference/html/scheduling.html#scheduling-annotation-support)
* [Spring Framework's cron expression syntax parser](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html)

[zip]: https://github.com/springframework-meta/gs-scheduling-tasks/archive/master.zip
