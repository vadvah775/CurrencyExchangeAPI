package com.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static Optional<String> validateCurrencyCode(String code) {
        if (code == null || code.trim().isEmpty() || code.equals("/")) {
            return Optional.empty();
        }
        String currencyCode = code;
        if (code.charAt(0) == '/')
            currencyCode = code.substring(1);

        if (currencyCode.matches("[A-Z]{3}$")) {
            return Optional.of(currencyCode);
        }
        return Optional.empty();
    }

    public static Optional<List<String>> validateTwoCurrencyCodes(String code) {
        if (code == null || code.equals("/")) {
            return Optional.empty();
        }
        String formattedCode = code;
        if (code.charAt(0) == '/')
            formattedCode = code.substring(1);

        if (formattedCode.length() != 6) {
            return Optional.empty();
        }
        Optional<String> code1 = validateCurrencyCode(formattedCode.substring(0, 3));
        Optional<String> code2 = validateCurrencyCode(formattedCode.substring(3, 6));
        if (code1.isEmpty() || code2.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(List.of(code1.get(), code2.get()));
    }

    public static Optional<BigDecimal> validateBigDecimal(String str) {
        try {
            double num = Double.parseDouble(str);
            return Optional.of(BigDecimal.valueOf(num).setScale(4, RoundingMode.HALF_UP));
        } catch (NullPointerException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<BigDecimal> validatePATCHRequest(String requestBody) {
        if (requestBody == null) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile("rate=([+-]?\\d+(\\.\\d+)?)(?=\\D|$)");
        Matcher matcher = pattern.matcher(requestBody);
        if (matcher.find()) {
            try {
                String validRate = matcher.group(1);
                double rate = Double.parseDouble(validRate);
                return Optional.of(BigDecimal.valueOf(rate));
            } catch (NullPointerException | NumberFormatException e){
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
