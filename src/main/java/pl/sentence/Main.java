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

        try (InputStream in = System.in;
             OutputStream out = System.out) {
            SentenceConfig sentenceConfig = choseOutputType(getParam(args));
            SentenceProcessor sentenceProcessor = new SentenceProcessor(in, out, sentenceConfig);
            sentenceProcessor.process();
            logger.info("The program ended with successful\n\n");
        } catch (SentenceParserException | IOException e) {
            logger.error(e.getMessage());
            logger.info("The program ended with failure!!!\n\n\n");
        }
    }

    private static SentenceConfig choseOutputType(String type) throws SentenceParserException {
        SentenceConfig sentenceConfig = new SentenceConfig();
        sentenceConfig.setLanguageConfig(LanguageConfig.US);

        switch (type) {
            case "csv":
                sentenceConfig.setFormatOut(FormatOut.CSV);
                logger.info("CSV data format was selected");
                break;
            case "xml":
                sentenceConfig.setFormatOut(FormatOut.XML);
                logger.info("XML data format was selected");
                break;
            case "xmlflat":
                sentenceConfig.setFormatOut(FormatOut.XML_FLAT);
                logger.info("XML - flat data format was selected");
                break;
            default:
                String errorMessage = "missing or wrong parameter, expected:  -csv,  -xml, -xmlflat";
                System.err.println(errorMessage);
                throw new SentenceParserException(errorMessage);
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
