import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Automation {

	public static void main(String[] args) {
		
		System.out.println("RODANDO CONSULTAS LAUDO");

		ExcelManager excel = new ExcelManager();

		ArrayList<Map<String, Object>> excelData = excel.readExcelData(excel.getFile());
		String savePath = FileChooser.getInstance().choosePath();

		BrowserManager browser = new BrowserManager(excelData, savePath);
		
		browser.execute();


	}

}
