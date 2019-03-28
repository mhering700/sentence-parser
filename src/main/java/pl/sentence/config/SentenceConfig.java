package pl.sentence.config;

import java.util.StringJoiner;

public class SentenceConfig {

    private FormatOut formatOut;
    private LanguageConfig languageConfig;
    private String eolCsv = "\n";
    private String eolXml = "\n";

    public FormatOut getFormatOut() {
        return formatOut;
    }

    public void setFormatOut(FormatOut formatOut) {
        this.formatOut = formatOut;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;

    }

    public String getEolCsv() {
        return eolCsv;
    }

    public String getEolXml() {
        return eolXml;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").
                add("formatOut= " + formatOut).
                add("languageConfig" + languageConfig.getLocale()).
                add("eolCsv=\\" + eolCsv).
                add("eolXml=\\" + eolXml).toString();
    }
}
