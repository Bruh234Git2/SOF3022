package poly.edu.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertTrue;

public class CategoryAdminTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize(); // Mở toàn màn hình để dễ quan sát
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // **BƯỚC 1: ĐĂNG NHẬP**
        driver.get("http://localhost:8080/auth/login");
        driver.findElement(By.name("email")).sendKeys("admin@example.com"); // Thay bằng email admin của bạn
        driver.findElement(By.name("password")).sendKeys("123456"); // Thay bằng mật khẩu admin của bạn
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // **BƯỚC 2: ĐIỀU HƯỚNG ĐẾN TRANG CẦN TEST**
        // Chờ đến khi đăng nhập thành công và chuyển hướng xong
        wait.until(ExpectedConditions.urlContains("/pages/home")); 
        driver.get("http://localhost:8080/admin/category");

        // **BƯỚC 3: KIỂM TRA ĐÃ VÀO ĐÚNG TRANG ADMIN CHƯA**
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[text()='Loại hàng']")));
    }

    // Các test case giữ nguyên...
    @Test
    public void testCreateNewCategory() {
        driver.findElement(By.cssSelector("button[data-bs-target='#modalCategory']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalCategory")));
        driver.findElement(By.id("catName")).sendKeys("Selenium Test Category");
        driver.findElement(By.id("catDesc")).sendKeys("Description added by Selenium");
        driver.findElement(By.id("btnSaveCat")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("catBody"), "Selenium Test Category"));
        String bodyText = driver.findElement(By.id("catBody")).getText();
        assertTrue(bodyText.contains("Selenium Test Category"));
    }

    @Test
    public void testUpdateCategory() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#catBody tr"), 0));
        WebElement firstEditButton = driver.findElement(By.cssSelector("#catBody tr:first-child [data-action='edit']"));
        firstEditButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalCategory")));
        WebElement catNameInput = driver.findElement(By.id("catName"));
        catNameInput.clear();
        catNameInput.sendKeys("Updated by Selenium");
        driver.findElement(By.id("btnSaveCat")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("catBody"), "Updated by Selenium"));
        String bodyText = driver.findElement(By.id("catBody")).getText();
        assertTrue(bodyText.contains("Updated by Selenium"));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}