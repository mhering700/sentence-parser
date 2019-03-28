package pl.sentence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.sentence.config.FormatOut;
import pl.sentence.config.LanguageConfig;
import pl.sentence.config.SentenceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting the program ...");
        SentenceConfig sentenceConfig = choseOutputType(getParam(args));

        try (InputStream in = System.in;
             OutputStream out = System.out) {
            SentenceProcessor sentenceProcessor = new SentenceProcessor(in, out, sentenceConfig);
            sentenceProcessor.process();
            logger.info("The program was successful");
        } catch (SentenceParserException | IOException e) {
            logger.error(e.getMessage());
            logger.info("The program ended in failure");
        }
    }

    private static SentenceConfig choseOutputType(String type) {
        SentenceConfig sentenceConfig = new SentenceConfig();
        sentenceConfig.setLanguageConfig(LanguageConfig.US);

        switch (type) {
            case "csv":
                sentenceConfig.setFormatOut(FormatOut.CSV);
                logger.info("The record was selected in csv format");
                break;
            case "xml":
                sentenceConfig.setFormatOut(FormatOut.XML);
                logger.info("The record was selected in xml pretty format");
                break;
            case "xmlflat":
                sentenceConfig.setFormatOut(FormatOut.XML_FLAT);
                logger.info("The record was selected in xml flat format");
                break;
            default:
                logger.error("missing or wrong parameter");
        }
        return sentenceConfig;
    }

    private static String getParam(String[] args) {

        String regexPattern = "^-(csv|xml|xmlflat)";
        Pattern pattern = Pattern.compile(regexPattern);


        for (String param : args) {
            Matcher matcher = pattern.matcher(param);
            if (matcher.find()) {

                return param.substring(1);
            }

        }
        return "";

    }
}
