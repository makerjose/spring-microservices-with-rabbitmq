package com.josemaker.order_service.services;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class EmailValidator {

    // case sensitive validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // email validation method
    public String validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }

        boolean isValidFormat = EMAIL_PATTERN.matcher(email.trim()).matches();
        if (!isValidFormat) {
            return "Invalid email format";
        }

        // additional domain verification
        String[] parts = email.split("@");
        if (parts.length < 2 || parts[1].contains("temp-mail")) {
            return "Disposable emails are not allowed";
        }

        return "valid"; // indicates successful validation
    }
}
