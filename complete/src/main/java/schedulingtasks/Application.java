package schedulingtasks;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(
				UserService.class, CleanOutUnactivatedAccounts.class);
		UserService userService = ctx.getBean(UserService.class);
		
		while (true) {
			userService.createNewUser(RandomStringUtils.randomAlphabetic(8));
			Thread.sleep(10000);

		}
	}
	
}
