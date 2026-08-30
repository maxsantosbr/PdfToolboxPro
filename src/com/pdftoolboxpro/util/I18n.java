package com.pdftoolboxpro.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private static ResourceBundle bundle;
    private static Locale currentLocale = Locale.ENGLISH;

    static {
        setLocale(Locale.ENGLISH);
    }

        public static void setLocale(Locale locale) {
        try {
            currentLocale = locale;
            ResourceBundle.clearCache();
            bundle = ResourceBundle.getBundle("com.pdftoolboxpro.i18n.Bundle", locale, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));
        } catch (Exception e) {
            ResourceBundle.clearCache();
            bundle = ResourceBundle.getBundle("com.pdftoolboxpro.i18n.Bundle", Locale.ENGLISH, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));
        }
    }
    
    public static String get(String key) {
        try { return bundle.getString(key); } 
        catch (Exception e) { return "!" + key + "!"; }
    }
    
    public static Locale getCurrentLocale() { return currentLocale; }
}
