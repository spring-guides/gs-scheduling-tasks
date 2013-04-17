#Getting Started with Scheduling Tasks

This guide will help walk you through the basic steps of setting up scheduled tasks.

##Prerequisites

### Maven
```xml
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-context</artifactId>
	<version>3.2.2.RELEASE</version>
</dependency>
<dependency>
	<groupId>commons-lang</groupId>
	<artifactId>commons-lang</artifactId>
	<version>2.6</version>
</dependency>
```
### Gradle
```
'org.springframework:spring-context:3.2.2.RELEASE'
'commons-lang:commons-lang:2.6'
```

Visit either **Getting Started with Maven** or **Getting Started with Gradle** if you need more details on setting up either system to build this app.

## Problem we need to solve

We have built a pretty simple application that lets users register new accounts and then activate them. Now we need to poll periodically for people that registered but never activated their accounts, and delete them if they are more than two days old.

## Let's write some code!

First, let's build a simple app. We aren't going to implement all that functionality of registering users, but instead code a relatively simple simulator.

```java
import org.springframework.context.*;
import org.apache.commons.lang.*;

public class MyApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = new ApplicationContext(Config.class);
		while (true) {
			UserService userService = ctx.getBean(UserService.class);
			userService.createNewUser(RandomStringUtils.random(8));
			Thread.sleep(60000);
		}
	}
}
```

This app creates random username and registers them with our **UserService** to emulate real people registering with our app. 

When we launch our app, it will look into **Config** to find the beans we need. In our case, we only need one: **UserService**. Let's define that configuration.

```java
import org.springframework.???;

@Configuration
public class Config {

	@Bean
	public UserService userService() {
		return new UserService();
	}
}
```

The last step we need to build our application is creating a **UserService** that lets us register new users.

```java
import java.util.*;

public class UserService {
	// Tracks when a certain user was created.
	public Map<String, Date> users = new HashMap<String, Date>;
	
	public void createNewUser(String username) {
		System.out.println("User " + username + " has just registered!");
		users.put(username, new Date());
	}
}
```

> Normally we would tag properties like **createdUsers** with **private** and then create getters and setters. For the sake of brevity, we are side-stepping that by simply making our in-memory datastore **public**.

Now let's run our new app!

```
mvn package ; mvn exec:java
```

```text
. . .text showing app running
```

Okay, we can see the users being created every 60 seconds. They get stored into a Java Map in memory. That means that if we shutdown the app and restart it, all the data is lost. If this was a real app, we would certainly store that data elsewhere. For for now, this is good enough.

It's time to add a scheduled task. In this situation, we need to iterate over each user, check the date they were added, and if it's too old, remove it from the map.

```java
import org.springframework.context.*;

public class CleanOutUnactivatedAccounts {
	@Autowired
	private UserService userService;
	
	@Scheduled(fixedRate=5000)
	public void lookForOldAccountsAndDeleteThem() {
		System.out.println("Checking for old accounts…");
		Iterator<Map.Entry<String, Date>> entries = userService.users.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Date> entry = entries.next();
			if (entry.getValue() > /*30 seconds*/) {
				System.out.println("User " + entry.getKey() + " is over 30 seconds old. Deleting.");
				userService.users.remove(entry.getKey());
			}
		}
	}
}
```

> It's possible to iterate many different ways through a map, but using an iterator helps prevents ConcurrentModificationExceptions in the event we find an expired user that we must remove.

The key component to making it perform scheduled tasks is the @Scheduled annotation applied to our method. In this code block we have it configured to run the method every five seconds, regardless of how long the method takes to run.

> @Scheduled(fixedRate=xyz) measures the xyz time at the beginning of the task. @Scheduled(fixedDelay=xyz) measures the xyz time at the end of the task, making it more pragmatic for long running jobs.

A couple things are needed to make the @Scheduled annotation work. First of all, we have configured it to automatically inject a copy of our UserService we defined earlier so we can access the user data using the @Autowired annotation. Second, we need to update our Config class to also create an instance when our app starts.

```java
import org.springframework.???;

@Configuration
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

With all this in place, we can re-start our app, and watch the schedule job run.

```
mvn package ; mvn exec:java
```

```text
. . .text showing app running
```

## Alternative Configurations

With this guide, we have so far seen how to set up a simple scheduled task based on a fixed time interval. A more advanced option is to use a cron expression.

The code below shows an example of coding a class that would generate reports on a daily, weekly, and monthly basis.

```java
import org.springframework.context.*;

public class GenerateReports {
	@Scheduled(cron="0 5 * * *") // execute at 5:00am every day
	public void generateDailyReport() {
	}
	
	@Scheduled(cron="15 14 * * 1") // execute at 2:15pm every Monday
	public void generateWeeklyReport() {
	}
	
	@Scheduled(cron="45 9 15 * *") // execute at 9:45am on the 15th of the month
	public void generateMonthlyReport() {
	}
}
```

To activate these jobs, we need to add another method to our app's Config class.
```java
import org.springframework.???;

@Configuration
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

> It is impossible to capture every permutation of a cron expression with code samples. For more details on cron expressions, visit [cron](http://en.wikipedia.org/wiki/Cron) on wikipedia.

## External Links
* [Spring Framework 3.2.2.RELEASE official docs for scheduling tasks](http://static.springsource.org/spring/docs/3.2.2.RELEASE/spring-framework-reference/html/scheduling.html#scheduling-annotation-support)