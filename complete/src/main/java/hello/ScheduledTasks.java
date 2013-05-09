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
