package poly.edu.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ProductAdminTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        // **LOGIC ĐĂNG NHẬP**
        driver.get("http://localhost:8080/auth/login");
        driver.findElement(By.name("email")).sendKeys("admin@example.com"); // Thay bằng email admin
        driver.findElement(By.name("password")).sendKeys("123456"); // Thay bằng mật khẩu admin
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/pages/home"));
        driver.get("http://localhost:8080/admin/product");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[text()='Sản phẩm']")));
    }

    // Các test case giữ nguyên...
    @Test
    public void testSearchProduct() {
        driver.findElement(By.id("kw")).sendKeys("Áo");
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        String bodyText = driver.findElement(By.id("prodBody")).getText();
        assertTrue(bodyText.toLowerCase().contains("áo"));
    }

    @Test
    public void testCreateNewProduct() {
        driver.findElement(By.cssSelector("button[data-bs-target='#modalProduct']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalProduct")));
        driver.findElement(By.id("pName")).sendKeys("Selenium Product");
        driver.findElement(By.id("pPrice")).sendKeys("123000");
        driver.findElement(By.id("pImg")).sendKeys("https://via.placeholder.com/150");
        Select categorySelect = new Select(driver.findElement(By.id("pCat")));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#pCat option"), 1));
        categorySelect.selectByIndex(1);
        driver.findElement(By.id("pDesc")).sendKeys("Product created via automation.");
        driver.findElement(By.id("btnSaveProd")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("prodBody"), "Selenium Product"));
        String bodyText = driver.findElement(By.id("prodBody")).getText();
        assertTrue(bodyText.contains("Selenium Product"));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}