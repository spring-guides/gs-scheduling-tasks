package schedulingtasks;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class CleanOutUnactivatedAccounts {
	@Autowired
	public UserService userService;
	
	//TODO(4/17/2013): This iteration pattern does NOT protect against ConcurrentModificationException
	@Scheduled(fixedRate=5000)
    public void lookForOldAccountsAndDeleteThem() {
        System.out.println("Checking for old accounts");
        Iterator<Map.Entry<String, Date>> entries = userService.users.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Date> entry = entries.next();
            if (new Date().getTime() - entry.getValue().getTime() > 30000) {
                System.out.println("User " + entry.getKey() + " is over 30 seconds old. Deleting.");
                userService.users.remove(entry.getKey());
            }
        }
    }
}
