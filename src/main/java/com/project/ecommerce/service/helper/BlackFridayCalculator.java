package com.project.ecommerce.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class BlackFridayCalculator {

    // Black Friday tarihini hesaplayacak metod
    public LocalDate calculateBlackFriday() {
        int currentYear = LocalDate.now().getYear(); // Geçerli yılı al
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, 11, 1); // Kasım ayının ilk günü
        return firstDayOfMonth.with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.FRIDAY));
    }

    // İndirim uygulama metodunu tanımlayabilirsiniz
    public double applyDiscount(double totalPrice, int discountPercentage) {
        double discountAmount = (discountPercentage / 100.0) * totalPrice;
        return totalPrice - discountAmount;
    }

}

