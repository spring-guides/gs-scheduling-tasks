package schedulingtasks;

import org.springframework.scheduling.annotation.Scheduled;

public class GenerateReports {
	@Scheduled(cron="0 0 5 * * *") // execute at 5:00:00am every day
	public void generateDailyReport() {
	}
	
	@Scheduled(cron="0 15 14 * * 1") // execute at 2:15:00pm every Monday
	public void generateWeeklyReport() {
	}
	
	@Scheduled(cron="0 45 9 15 * *") // execute at 9:45:00am on the 15th of the month
	public void generateMonthlyReport() {
	}
}
