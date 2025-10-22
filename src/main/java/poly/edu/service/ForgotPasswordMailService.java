package poly.edu.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor; // Dùng @RequiredArgsConstructor cho gọn
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Tự động tiêm (inject) các trường final
public class ForgotPasswordMailService {

    private final JavaMailSender mailSender; // Giả sử bạn dùng mailSender
    // private final MailService mailService; // Hoặc dùng MailService của bạn

    /**
     * Sửa lại phương thức này để nhận 2 tham số
     * @param toEmail Email của người nhận
     * @param resetUrl Link đầy đủ để reset (đã được tạo ở Controller)
     * @throws MessagingException
     */
    public void sendResetLink(String toEmail, String resetUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Nội dung email (viết bằng HTML)
        // Chúng ta không cần truy vấn Account ở đây nữa, 
        // vì Controller đã làm điều đó rồi.
        String htmlContent = String.format("""
            <html>
                <body>
                    <h2>Yêu cầu đặt lại mật khẩu</h2>
                    <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>
                    <p>Vui lòng nhấp vào liên kết bên dưới để đặt lại mật khẩu của bạn:</p>
                    <p>
                        <a href="%s" style="background-color: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;">
                            Đặt lại mật khẩu
                        </a>
                    </p>
                    <p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>
                    <p>Lưu ý: Liên kết sẽ hết hạn sau 15 phút.</p>
                </body>
            </html>
        """, resetUrl); // Chèn resetUrl vào thẻ <a>

        helper.setTo(toEmail);
        helper.setSubject("Yêu cầu đặt lại mật khẩu");
        helper.setText(htmlContent, true); // true để chỉ định đây là HTML

        mailSender.send(message);
        
        // Nếu bạn dùng MailService tùy chỉnh:
        // mailService.send(toEmail, "Yêu cầu đặt lại mật khẩu", htmlContent);
    }
}