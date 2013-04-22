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
