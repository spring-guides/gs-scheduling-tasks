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
