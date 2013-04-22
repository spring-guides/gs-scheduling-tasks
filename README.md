# Getting Started with Scheduling Tasks

This [Getting Started guide](https://github.com/springframework-meta/gs-scheduling-tasks) will walk you through the basic steps of setting up scheduled tasks using Spring.

To help you get started, we've provided an initial project structure as well as a completed project for you on GitHub.

```sh
$ git clone git://github.com/springframework-meta/gs-scheduling-tasks.git
```

If you clone that repository, you will find one folder called **start** which contains the basic parts of this guide setup.

There is another folder called **complete** which contains all the code from this guide, ready to run.

Before we can create a scheduled task, there's some initial project setup that's required. Or, you can skip straight to the [fun part](#adding-a-scheduled-task).

## Setting up a project

### Selecting Dependencies

The sample in this Getting Started guide uses Spring's task scheduler which is found in Spring Context as well as the Apache Commons Language library. Therefore, the following library dependencies are needed in the project's build configuration:

- 'org.springframework:spring-context:3.2.2.RELEASE'
- 'commons-lang:commons-lang:2.6'

Refer to [Getting Started with Gradle](https://github.com/springframework-meta/gs-gradle/blob/master/README.md) or [Getting Started with Maven](https://github.com/springframework-meta/gs-maven/blob/master/README.md) for details on how to include these dependencies in your build.

### Problem we need to solve

For this guide, let's imagine we have built a simple application where users register new accounts and then activate them. You have discovered that we need to poll periodically for people that registered but never activated their accounts, and delete them if they are more than two days old.

### Creating an Application

First, we need to build that simple application. Instead of implementing all that functionality of registering users, let's code a simple simulator instead. We can do it by adding the following code to **App.java**.

```java
package schedulingtasks;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = ctx.getBean(UserService.class);
		while (true) {
			userService.createNewUser(RandomStringUtils.randomAlphabetic(8));
			Thread.sleep(10000);
		}
		
	}
}
```

This application does two things. First, when we launch our app, it creates a Spring application context driven by our `Config` class. `Config` contains our declared beans in pure Java code. We retrieve the `UserService` bean in order to complete our second step.

Next, the application goes into a loop where it creates random user names every ten seconds and registers them with our `UserService` to emulate real people registering with our application.

### Creating a Configuration Class

Now that we have written our base application, we need to configure the Spring application context used by `App`. Let's do that next by copying the following code into **Config.java**.

```java
package schedulingtasks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Bean
	public UserService userService() {
		return new UserService();
	}
}
```

The `@Configuration` annotation provides a signal to Spring that this class contains bean definitions. The `@Bean` annotation registers the returned by `userService`.

### Creating a User Service

The last step we need to build our application is creating a `UserService` that lets us register new users. We can do that by copying the following code into **UserService.java**.

```java
package schedulingtasks;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserService {

	public Map<String, Date> users = new HashMap<String, Date>();
	
	public void createNewUser(String username) {
		System.out.println("User " + username + " has just registered!");
		users.put(username, new Date());
	}
}
```

> It's a common Java convention to tag properties like `users` with `private` and then create getters and setters. For the sake of brevity, we are side-stepping that by simply making our in-memory datastore `public`.

Our service only has one method: `createNewUser`. It stores the new user as well as the time it was created in a local map.

### Building and Running Our Application

With all these parts defined, we are ready to run it!

```
./gradlew run
```

We can also run it with maven.

```
mvn compile exec:java
```
> With maven's exec plugin, it's important to run the compile task each time to make sure it uses our latest changes.

We should expect to see something like this.

```text
Apr 18, 2013 4:12:54 PM org.springframework.context.support.AbstractApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@4e0c2b07: startup date [Thu Apr 18 16:12:54 CDT 2013]; root of context hierarchy
Apr 18, 2013 4:12:55 PM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@2eaafcb8: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,config,org.springframework.context.annotation.ConfigurationClassPostProcessor.importAwareProcessor,userService]; root of factory hierarchy
User AUKDJGgy has just registered!
User sHQrJPyT has just registered!
User HANflSdG has just registered!
```

Users are being created every ten seconds. They get stored into the `UserService` map, which in this guide, is purely in-memory and not persisted anywhere. That means that if we shutdown the application and restart it, all the data will be lost. If this was a real application, we would probably want to store that data somewhere. For now, this is good enough.

## Adding a Scheduled Task

Now that we've setup our basic application, it's time to add a scheduled task. In the problem description, we need to poll the list of registered users and delete any that are too old. Normally, this might be over some time span of hours or even days. But for this guide, we will instead delete any users that over thirty seconds old.

To do this, we need to iterate over each user, check the date they were added, and if it's too old, remove it from the map.

```java
package schedulingtasks;

import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

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

The key component to making it perform scheduled tasks is the `@Scheduled` annotation applied to our method. In this code block we have it configured to run the method every five seconds, regardless of how long the method takes to run.

The example above uses `fixedRate`. 
- `@Scheduled(fixedRate=<milliseconds>)` measures the time interval by starting at the beginning of the task. 
- `@Scheduled(fixedDelay=<milliseconds>)` measures the time interval by starting at the end of the task.

Imagine the task is very long running, perhaps taking ten minutes to run. If the scheduled task was configured with a `fixedRate` of ten seconds, multiple instances would be launched and inevitably consume too many resources. But if it was configured with a `fixedDelay` of ten seconds instead, one instance would run to completion before scheduling the next task.

## Activating our Scheduled Task

A few things are needed to make the `@Scheduled` annotation work. 
* First, we ask Spring to inject a copy of our `UserService` we defined earlier using the `@Autowired` annotation.
* Second, we need register `CleanOutUnactivatedAccounts` in Spring's application context by adding a new method to our `Config` class to create an instance of our task.
* Finally, we need to annotate our `Config` class with `@EnableScheduling` so our application will look for scheduled tasks.

We already have the `UserService` wired up in the code just above. Now let's update `Config` with the right settings.

```java
package schedulingtasks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class Config {
	
	@Bean
	public UserService userService() {
		return new UserService();
	}
	
	@Bean
	public CleanOutUnactivatedAccounts cleanoutUnactivatedAccounts() {
		return new CleanOutUnactivatedAccounts();
	}
}
```

## Running our Application with Scheduled Tasks

With all this in place, we can re-start our app, and watch the scheduled job run.

```
./gradlew run
```
Or run the new version with maven.

```
mvn compile exec:java
```

We should expect to see something like this.

```text
Apr 18, 2013 4:25:53 PM org.springframework.context.support.AbstractApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@2d523d40: startup date [Thu Apr 18 16:25:53 CDT 2013]; root of context hierarchy
Apr 18, 2013 4:25:54 PM org.springframework.context.support.AbstractApplicationContext$BeanPostProcessorChecker postProcessAfterInitialization
INFO: Bean 'org.springframework.scheduling.annotation.SchedulingConfiguration' of type [class org.springframework.scheduling.annotation.SchedulingConfiguration$$EnhancerByCGLIB$$c42fb15] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
Apr 18, 2013 4:25:54 PM org.springframework.beans.factory.support.DefaultListableBeanFactory preInstantiateSingletons
INFO: Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@5b187658: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,config,org.springframework.context.annotation.ConfigurationClassPostProcessor.importAwareProcessor,org.springframework.scheduling.annotation.SchedulingConfiguration,org.springframework.context.annotation.internalScheduledAnnotationProcessor,userService,cleanoutUnactivatedAccounts]; root of factory hierarchy
Checking for old accounts
User IKhblLlZ has just registered!
Checking for old accounts
Checking for old accounts
User REyASXAP has just registered!
Checking for old accounts
Checking for old accounts
User hsDnCNtn has just registered!
Checking for old accounts
Checking for old accounts
User YuKyDYTq has just registered!
Checking for old accounts
User IKhblLlZ is over 30 seconds old. Deleting.
```

In the text up above, we can see extra messages logged by our scheduled task. It also shows a the first user being delete after thirty seconds.

## Scheduling More Complex Tasks

With this guide, we have so far seen how to set up a simple scheduled task based on a fixed time interval. A more advanced option is to use a cron expression.

The code below shows an example of coding a class that would generate reports on a daily, weekly, and monthly basis. We can plug it in by copying it into **GenerateReports.java**.

```java
package schedulingtasks;

import org.springframework.scheduling.annotation.Scheduled;

public class GenerateReports {
	@Scheduled(cron="0 0 5 * * *") // execute at 5:00:00am every day
	public void generateDailyReport() {
	}
	
	@Scheduled(cron="0 15 14 * * 1") // execute at 2:15:00pm every Monday
	public void generateWeeklyReport() {
	}
	
	@Scheduled(cron="0 45 9 15 * *") // execute at 9:45:00am on the 15th of the month
	public void generateMonthlyReport() {
	}
}
```

To activate these jobs, we need to add another method to our application's `Config` class.

```java
package schedulingtasks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class Config {
	
	@Bean
	public UserService userService() {
		return new UserService();
	}
	
	@Bean
	public CleanOutUnactivatedAccounts cleanoutUnactivatedAccounts() {
		return new CleanOutUnactivatedAccounts();
	}
	
	@Bean
	public GenerateReports generateReports() {
		return new GenerateReports();
	}
}
```

It is impossible to demonstrate every permutation of a cron expression with code samples. Hopefully, these examples provide a starting point. Details about cron syntax are shown below.

The pattern is a list of six single space-separated fields representing second, minute, hour, day, month, and weekday. Month and weekday names can be given as the first three letters of the English names.

```
cronExpression: "s m h D M W"
                 | | | | | `- Day of Week, 1-7 or SUN-SAT
                 | | | | `- Month, 1-12 or JAN-DEC
                 | | | `- Day of Month, 1-31
                 | | `- Hour, 0-23
                 | `- Minute, 0-59
                 `- Second, 0-59
```

Congratulations! You have put together a handful of scheduled tasks and quickly wired them into your application. This technique works inside any type of application, web or command-line.

## External Links
* [Spring Framework 3.2.2.RELEASE official docs for scheduling tasks](http://static.springsource.org/spring/docs/3.2.2.RELEASE/spring-framework-reference/html/scheduling.html#scheduling-annotation-support)