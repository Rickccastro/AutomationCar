import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v122.page.Page;
import org.openqa.selenium.devtools.v122.page.Page.PrintToPDFResponse;


public class BrowserManager {

	private ArrayList<Map<String, Object>> excelData;
	private String savePath;
	private Robot robot;
	private WebDriver mDriver;

	public BrowserManager(ArrayList<Map<String, Object>> excelData, String savePath) {
		this.excelData = excelData;
		this.savePath = savePath;
		
	}

	private WebDriver getDriver() {
		if(mDriver == null) {
			
			ChromeOptions options = new ChromeOptions();
			
			options.addArguments("--kiosk-printing");
			
			/* passa o padrao do diretorio de download */
			options.addArguments("download.default_directory=" + savePath);
			
			options.addArguments("--print-to-path=" + savePath);
			/* faz o download automaticamente para o local configurado no navegador. */
			options.addArguments("download.prompt_for_download=false");
			
			/* você instrui o navegador Chrome a desativar o bloqueio de pop-ups */
			options.addArguments("disable-popup-blocking");
			
			options.addArguments("plugins.always_open_pdf_externally=true");

			
			Map<String,Object> prefs = new HashMap<String,Object>();
			
			prefs.put("download.default_directory", savePath);
			options.setExperimentalOption("prefs", prefs);
//		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
			
			mDriver = new ChromeDriver(options);
			
			mDriver.get("https://www.companyconferi.com.br/areadocliente/consultas");
		}

		return mDriver;
	}

	private void executeQuery(WebDriver driver) {
		for (int i = 0; i < excelData.size(); i++) {
//			System.out.println(excelData.get(i).get("placa")+" - "+excelData.get(i).get("data_vistoria"));
			String placa = (String) excelData.get(i).get("placa");
			consulta(driver, "");
			consulta(driver, placa);
			clickOnConsulta(driver);
			boolean hasLaudo = clickGoToLaudo(driver);
			if (hasLaudo) {
				switchTabs(driver, 1, false);
				clickPrint(driver, placa);
				switchTabs(driver, 0, true);
			}

		}

	}

	private void loginOnPage(WebDriver driver) {
		try {
			cookies(driver);
			Thread.sleep(5000);
			login(driver);
			Thread.sleep(10000);
			closePopUp(driver);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void execute() {
		WebDriver driver = getDriver();
		loginOnPage(driver);
		goToConsulta(driver);
		executeQuery(driver);
	}

	private Robot getRobot() {
		if (robot == null) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return robot;
	}


	private void clickPrint(WebDriver driver, String placa) {
		
		DevTools devTools = ((ChromeDriver) getDriver()).getDevTools();
        devTools.createSession();

        PrintToPDFResponse pdf = devTools.send(Page.printToPDF(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(),Optional.of(true)));

        // Decode and save the PDF
        byte[] pdfBytes = Base64.getDecoder().decode(pdf.getData());
        try {
			java.nio.file.Files.write(java.nio.file.Paths.get(savePath + "/" + placa + ".pdf"), pdfBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void switchTabs(WebDriver driver, int index, boolean hasClose) {
		try {
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			if (hasClose) {
				driver.close();
			}
			driver.switchTo().window(tabs.get(index));
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean clickGoToLaudo(WebDriver driver) {
		try {
			WebElement botaoLaudo = driver.findElement(By.xpath(
					"//a[@class='btn-floating btn-large waves-effect red accent-2 modal-trigger z-depth-5 ng-scope']"));
			if (botaoLaudo != null) {
				botaoLaudo.click();
				Thread.sleep(1000);
				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());
		}
		return false;
	}

	private void clickOnConsulta(WebDriver driver) {
		try {
			WebElement botaoSearch = driver.findElement(By.xpath("//button[@class='waves-effect waves-light btn']"));
			botaoSearch.click();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private void consulta(WebDriver driver, String placa) {
		try {
			WebElement inputConsulta = driver.findElement(By.cssSelector("[title='Entre com a placa ou chassi']"));
			if (placa.isEmpty()) {
				inputConsulta.clear();
			} else {
				inputConsulta.sendKeys(placa);
			}
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	private void goToConsulta(WebDriver driver) {
		try {
			WebElement botaoConsulta = driver.findElement(By.id("mn2"));
			botaoConsulta.click();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void login(WebDriver driver) {

		try {
			WebElement botao = driver.findElement(By.id("btnSuaConta"));
			if (botao != null) {
				botao.click();
				Thread.sleep(1000);
				WebElement botaoLog = driver.findElement(By.name("logNome"));
				botaoLog.sendKeys("seuUser");
				Thread.sleep(1000);
				WebElement botaoPassword = driver.findElement(By.id("senhaLog"));
				Thread.sleep(1000);
				botaoPassword.sendKeys("senhadoseulogin");
				Thread.sleep(1000);
				WebElement botaoSend = driver.findElement(By.cssSelector("button.btn.btn-block.btn-info > span"));
				Thread.sleep(1000);
				botaoSend.click();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private void closePopUp(WebDriver driver) {
		try {
			WebElement popUp = driver.findElement(By.id("materialize-lean-overlay-1"));
			if (popUp != null) {
				popUp.click();
			}
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	private void cookies(WebDriver driver) {
		try {
			WebElement botaoCookies = driver.findElement(By.className("btn-cookies"));
			if (botaoCookies != null) {
				Thread.sleep(1000);
				botaoCookies.click();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println(e.getLocalizedMessage());

		}
	}

	private void write(Robot robot, String text) {
		// Escrevendo uma string

		for (char c : text.toCharArray()) {
			int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
			robot.delay(50); // Espera um pequeno intervalo entre cada caractere
		}
	}
}
