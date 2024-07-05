package com.project.ecommerce.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class HappyHourCalculator {

    // Happy Hour zamanı kontrolü
    public boolean isHappyHour(LocalDateTime orderDate) {
        return orderDate.getDayOfWeek() == DayOfWeek.MONDAY &&
                orderDate.toLocalTime().isAfter(LocalTime.of(13, 0)) &&
                orderDate.toLocalTime().isBefore(LocalTime.of(14, 0));
    }

    // Happy Hour indirimini uygulayacak metod
    public double applyDiscount(double totalPrice, int discountPercentage) {
        double discountAmount = (discountPercentage / 100.0) * totalPrice;
        return totalPrice - discountAmount;
    }
}
