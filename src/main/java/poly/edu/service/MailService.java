package poly.edu.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void send(String to, String subject, String body) {
        System.out.println("🔹 Gửi mail đến: " + to);
        System.out.println("🔹 Tiêu đề: " + subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            System.out.println("✅ Mail gửi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Gửi mail thất bại: " + e.getMessage());
        }
    }

}
