package pl.sentence.config;

import java.util.Locale;

public enum LanguageConfig {

    US(Locale.US, new String[]{
            "Dr.", "Prof.", "Mr.", "Mrs.", "Ms.", "Jr.", "Ph.D."
    });

    private String[] abbreviations;
    private Locale locale;


    LanguageConfig(Locale locale, String[] abbreviations) {
        this.locale = locale;
        this.abbreviations = abbreviations;
    }

    public String[] getAbbreviations() {
        return abbreviations;
    }

    public Locale getLocale() {
        return locale;
    }
}

