package hello;

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
