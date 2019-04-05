package com.github.nathan130200.qdg;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Util {
    public static final Locale PT_BR = new Locale("pt", "BR");
    
    public static String getCurrencyAsString(Double amount){
        return NumberFormat.getCurrencyInstance(PT_BR)
                .format(amount);
    }
}
