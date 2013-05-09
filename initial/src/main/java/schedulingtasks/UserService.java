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
