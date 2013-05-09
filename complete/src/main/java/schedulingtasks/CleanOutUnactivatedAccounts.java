package schedulingtasks;

import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
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
