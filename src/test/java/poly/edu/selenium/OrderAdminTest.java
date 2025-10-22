package poly.edu.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertTrue;

public class OrderAdminTest {

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
        driver.get("http://localhost:8080/admin/order");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[text()='Đơn hàng']")));
    }

    @Test
    public void testFilterOrdersByStatus() {
        // Đảm bảo có dữ liệu trong bảng trước khi lọc
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#orderBody tr"), 0));
        
        Select statusFilter = new Select(driver.findElement(By.id("statusFilter")));
        statusFilter.selectByValue("shipping");

        // Chờ bảng reload
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        
        WebElement body = driver.findElement(By.id("orderBody"));
        wait.until(ExpectedConditions.visibilityOf(body));
        
        String bodyText = body.getText();
        // Giả sử có đơn hàng "Đang giao" để test
        assertTrue(bodyText.contains("Đang giao"));
    }

    @Test
    public void testUpdateOrderStatus() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#orderBody tr"), 0));
        driver.findElement(By.cssSelector("#orderBody tr:first-child [data-action='edit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalOrder")));
        
        Select statusSelect = new Select(driver.findElement(By.id("oStatus")));
        statusSelect.selectByValue("completed");
        
        driver.findElement(By.id("btnSaveOrder")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#orderBody tr:first-child"), "Hoàn thành"));
        String firstRowText = driver.findElement(By.cssSelector("#orderBody tr:first-child")).getText();
        assertTrue(firstRowText.contains("Hoàn thành"));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}