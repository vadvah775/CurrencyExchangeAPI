package com.utils;

import java.util.Optional;

public class Validator {
    public static Optional<String> validateCurrencyCode(String code){
        if (code == null || code.equals("/")) {
            return Optional.empty();
        }
        String currencyCode = code;
        if(code.charAt(0) == '/')
            currencyCode = code.substring(1);

        if(currencyCode.matches("[A-Z]{3}$")){
            return Optional.of(currencyCode);
        }
        return Optional.empty();
    }
}
