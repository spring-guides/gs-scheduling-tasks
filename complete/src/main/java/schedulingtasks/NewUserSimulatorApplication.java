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
