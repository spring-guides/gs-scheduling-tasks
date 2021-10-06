:toc:
:spring_version: current
:icons: font
:source-highlighter: prettify
:project_id: gs-scheduling-tasks

This guide walks you through the steps for scheduling tasks with Spring.

== What You Will Build

You will build an application that prints out the current time every five seconds by using
Spring's `@Scheduled` annotation.

== What You Need

:java_version: 1.8
include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/prereq_editor_jdk_buildtools.adoc[]

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/how_to_complete_this_guide.adoc[]

[[scratch]]
== Starting with Spring Initializr

You can use this https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=scheduling-tasks&name=scheduling-tasks&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.scheduling-tasks[pre-initialized project] and click Generate to download a ZIP file. This project is configured to fit the examples in this tutorial.

To manually initialize the project:

. Navigate to https://start.spring.io.
This service pulls in all the dependencies you need for an application and does most of the setup for you.
. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
. Click *Generate*.
. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

NOTE: If your IDE has the Spring Initializr integration, you can complete this process from your IDE.

NOTE: You can also fork the project from Github and open it in your IDE or other editor.

=== Adding the `awaitility` Dependency

The tests in
`complete/src/test/java/com/example/schedulingtasks/ScheduledTasksTest.java`
require the `awaitility` library.

NOTE: Later versions of the `awaitility` library do not work for this test, so you have to
specify version 3.1.2.

To add the `awaitility` library to Maven, add the following dependency:

====
[source,xml]
----
<dependency>
  <groupId>org.awaitility</groupId>
  <artifactId>awaitility</artifactId>
  <version>3.1.2</version>
  <scope>test</scope>
</dependency>
----
====

The following listing shows the finished `pom.xml` file:

====
[source,xml]
----
include::complete/pom.xml[]
----
====

To add the `awaitility` library to Gradle, add the following dependency:

====
[source,text]
----
	testImplementation 'org.awaitility:awaitility:3.1.2'
----
====

The following listing shows the finished `build.gradle` file:

====
[source,xml]
----
include::complete/build.gradle[]
----
====

[[initial]]
== Create a Scheduled Task

Now that you have set up your project, you can create a scheduled task. The following
listing (from `src/main/java/com/example/schedulingtasks/ScheduledTasks.java`) shows how
to do so:

====
[source,java]
----
include::complete/src/main/java/com/example/schedulingtasks/ScheduledTasks.java[]
----
====

The `Scheduled` annotation defines when a particular method runs.

NOTE: This example uses `fixedRate`, which specifies the interval between method
invocations, measured from the start time of each invocation. There are
https://docs.spring.io/spring/docs/{spring_version}/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled[other options],
such as `fixedDelay`, which specifies the interval between invocations measured from the
completion of the task. You can also use https://docs.spring.io/spring/docs/{spring_version}/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html[`@Scheduled(cron=". . .")`]
expressions for more sophisticated task scheduling.

== Enable Scheduling

Although scheduled tasks can be embedded in web applications and WAR files, the simpler
approach (shown in the next listing) creates a standalone application. To do so,
package everything in a single, executable JAR file, driven by a good old Java `main()`
method. The following listing (from
`src/main/java/com/example/schedulingtasks/SchedulingTasksApplication.java`) shows the
application class:

====
[source,java]
----
include::complete/src/main/java/com/example/schedulingtasks/SchedulingTasksApplication.java[]
----
====

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/spring-boot-application-new-path.adoc[]

The
https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling-enable-annotation-support[`@EnableScheduling`]
annotation ensures that a background task executor is created. Without it, nothing gets
scheduled.

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/build_an_executable_jar_subhead.adoc[]

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/build_an_executable_jar_with_both.adoc[]

Logging output is displayed, and you can see from the logs that it is on a background
thread. You should see your scheduled task fire every five seconds. The following listing
shows typical output:

====
[source,text]
----
...
2019-10-02 12:07:35.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:35
2019-10-02 12:07:40.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:40
2019-10-02 12:07:45.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:45
2019-10-02 12:07:50.657  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:50
...
----
====

== Summary

Congratulations! You created an application with a scheduled task. Also, this technique
works in any type of application.

== See Also

The following guides may also be helpful:

* https://spring.io/guides/gs/spring-boot/[Building an Application with Spring Boot]
* https://spring.io/guides/gs/batch-processing/[Creating a Batch Service]

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/footer.adoc[]
