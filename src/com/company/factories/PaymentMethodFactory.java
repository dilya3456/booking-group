package com.company.factories;

import com.company.models.PaymentMethod;

public class PaymentMethodFactory {
    private PaymentMethodFactory() { }

    public static PaymentMethod fromString(String input) {
        if (input == null) throw new IllegalArgumentException("Payment method is null");
        String s = input.trim().toUpperCase();
        return PaymentMethod.valueOf(s);
    }
}
