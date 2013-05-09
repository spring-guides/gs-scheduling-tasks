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
 - cd into `gs-rest-service/start`
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
            <artifactId>spring-bootstrap-web-starter</artifactId>
        </dependency>
        <dependency>
        	<groupId>commons-lang</groupId>
        	<artifactId>commons-lang</artifactId>
        	<version>2.6</version>
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
compile "org.springframework.bootstrap:spring-bootstrap-web-starter:0.0.1-SNAPSHOT"
compile "commons-lang:commons-lang:2.6"
```

### Problem we need to solve

For this guide, let's imagine we have built a simple application where users register new accounts and then activate them. You have discovered that we need to poll periodically for people that registered but never activated their accounts, and delete them if they are more than two days old.

Creating a Configuration Class
------------------------------
The first step is to set up a simple Spring configuration class. It'll look like this:

`src/main/java/schedulingtasks/ScheduledConfiguration.java`

```java
package schedulingtasks;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SchedulerConfiguration {
	
}
```

This class is small and lightweight, but it does many things. It is the primary means to configure all the components in our application. It uses the [`@ComponentScan`](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/context/annotation/ComponentScan.html) which tells Spring to scan the `schedulingtasks` package for all the annotated component classes.

Creating a User Registration Service
------------------------------------
Next, we need a service that simulates storing user data. 

`src/main/java/schedulingtasks/UserService.java`

```java
package schedulingtasks;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class UserService {

	public Map<String, Date> users = new HashMap<String, Date>();
	
	public void createNewUser(String username) {
		System.out.println("User " + username + " has just registered!");
		users.put(username, new Date());
	}
}
```
> It's a common Java convention to tag properties like `users` with `private` and then create getters and setters. For the sake of brevity, we are side-stepping that by simply making our in-memory datastore `public`.

<a name="initial"></a>

Creating a Scheduled Task
-------------------------
Now that we've setup our basic application, it's time to add a scheduled task. In the problem description, we need to poll the list of registered users and delete any that are too old. Normally, this might be over some time span of hours or even days. But for this guide, we will instead delete any users that over thirty seconds old.

To do this, we need to iterate over each user, check the date they were added, and if it's too old, remove it from the map.


`src/main/java/schedulingtasks/CleanOutUnactivatedAccounts.java`

```java
package schedulingtasks;

import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanOutUnactivatedAccounts {
	
	@Autowired
	public UserService userService;
	
	@Scheduled(fixedRate=5000)
    public void lookForOldAccountsAndDeleteThem() {
        System.out.println("Checking for old accounts");
        Iterator<String> keys = userService.users.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (new Date().getTime() - userService.users.get(key).getTime() > 30000) {
                System.out.println("User " + key + " is over 30 seconds old. Deleting.");
                keys.remove();
            }
        }
    }
}
```

> It's possible to iterate many different ways through a map, but `keys.remove()` prevents a `ConcurrentModificationException`.

By tagging this class with `@Component`, it will be automatically picked up by `SchedulerConfiguration`.

The key component to making it perform scheduled tasks is the `@Scheduled` annotation applied to our method. In this code block we have it configured to run the method every five seconds, regardless of how long the method takes to run.

The example above uses `fixedRate`. 
- `@Scheduled(fixedRate=<milliseconds>)` measures the time interval by starting at the beginning of the task. 
- `@Scheduled(fixedDelay=<milliseconds>)` measures the time interval by starting at the end of the task.

Imagine the task is very long running, perhaps taking ten minutes to run. If the scheduled task was configured with a `fixedRate` of ten seconds, multiple instances would be launched and inevitably consume too many resources. But if it was configured with a `fixedDelay` of ten seconds instead, one instance would run to completion before scheduling the next task.

Creating an executable main class
---------------------------------

A couple of things are needed to make the `@Scheduled` annotation work. 

- First, we ask Spring to inject a copy of our `UserService` we defined earlier using the `@Autowired` annotation.
- Second, we need to annotate our `Config` class with `@EnableScheduling` so our application will look for scheduled tasks.

We already have the `UserService` wired up in the code just above. Now let's update `SchedulerConfiguration` with the right settings.

`src/main/java/schedulingtasks/SchedulingConfiguration.java`

```java
package schedulingtasks;

import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
public class SchedulerConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerConfiguration.class, args);
	}
	
}
```

`@EnableScheduling` tells Spring to look for `@Scheduled` methods and schedule them with its task executor.

The `@EnableAutoConfiguration` annotation has also been added: it provides a load of defaults (like looking for `CommandLineRunner`s) depending on the contents of your classpath, and other things.

Now let's code up a new user registration simulator.

`src/main/java/schedulingtasks/NewUserSimulatorApplication.java`

```java
package schedulingtasks;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NewUserSimulatorApplication implements CommandLineRunner {

	@Autowired
	UserService userService;
	
	@Override
	public void run(String... args) throws Exception {
		while (true) {
			userService.createNewUser(RandomStringUtils.randomAlphabetic(8));
			Thread.sleep(10000);
		}
	}
	
}
```

The `@Component` annotation allows it to be picked up automatically by Spring. By implementing the `CommandLineRunner` interface, it is automatically run by `SpringApplication.run()`.

Building an executable JAR
--------------------------

Add the following to your `pom.xml` file (keeping any existing properties or plugins intact):

`pom.xml`
```xml
<properties>
	<start-class>schedulingtasks.SchedulerConfiguration</start-class>
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

... new users start getting created every ten seconds ...
... after thirty seconds, old users start getting deleted ...

```

Congratulations! You have created an application with scheduled tasks and quickly wired them into your application. This technique works inside any type of application, web or command-line.

Related Resources
-----------------

* [Spring Framework 3.2.2.RELEASE official docs for scheduling tasks](http://static.springsource.org/spring/docs/3.2.2.RELEASE/spring-framework-reference/html/scheduling.html#scheduling-annotation-support)

[zip]: https://github.com/springframework-meta/gs-scheduling-tasks/archive/master.zip
