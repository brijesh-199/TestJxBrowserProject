package Jenkins;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class JenkinsDemoJob {
	
	@Test
	public void Jenkinstest(){
		System.out.println("Welcome to Test Jenkins World");
		
		WebDriver driver=new FirefoxDriver();
		driver.get("http://www.facebook.com");
		System.out.println(driver.getTitle());
		driver.quit();
			
	}

}
