package skovsharev.autotest;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import skovsharev.autotest.Sender;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class FirstTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setup() {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        driver = new FirefoxDriver(capabilities);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
        driver.get("https://google.ru");
    }

    @Test
    public void TestFirst() throws IOException {

        WebElement search = driver.findElement(By.cssSelector("[name='q']"));
        search.sendKeys("Центральный банк РФ");
        search.sendKeys(Keys.ENTER);
        WebElement site = driver.findElement(By.xpath("//*[text()='Центральный банк Российской Федерации']"));
        String oldTab = driver.getWindowHandle();
        site.click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.stalenessOf(search));
        ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());//Получение списка табов
        newTab.remove(oldTab);
        driver.switchTo().window(newTab.get(0));//Переключение на второй
        WebElement reception = wait.until(visibilityOfElementLocated(By.cssSelector("a[href='/Reception/']")));
        reception.click();
        WebElement send = driver.findElement(By.cssSelector("a[href='/Reception/Message/Register?messageType=Gratitude']"));
        send.click();
        WebElement text = driver.findElement(By.cssSelector("#MessageBody"));
        text.sendKeys("Тестовое задание");
        WebElement agree = driver.findElement(By.cssSelector("#_agreementFlag"));
        agree.click();
        File screenshot = ((TakesScreenshot) driver).
                getScreenshotAs(OutputType.FILE);
        String path = "./target/screenshots/" + screenshot.getName();
        FileUtils.copyFile(screenshot, new File(path));
        Sender sender = new Sender("sema.usu@gmail.com", "monitor241");

        sender.send("test","This is the test result", "sema.usu@gmail.com","skovsharev@mail.ru",path);

    }

    public static void tearDown() {
        driver.quit();
    }
}