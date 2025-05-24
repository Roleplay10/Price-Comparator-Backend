package com.roleplay10.price_comparator.service.notification;

import com.roleplay10.price_comparator.domain.entity.PriceAlert;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;

    @Override
    public void notifyTargetReached(PriceAlert alert, BigDecimal currentPrice) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(alert.getUser().getEmail());
        msg.setSubject("Price Alert: " + alert.getProduct().getName());
        msg.setText(String.format(
                "Good news! %s is now %s, below your target %s.",
                alert.getProduct().getName(),
                currentPrice, alert.getTargetPrice()
        ));
        mailSender.send(msg);
    }
}
