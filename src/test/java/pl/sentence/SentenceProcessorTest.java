package pl.sentence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.sentence.config.LanguageConfig;
import pl.sentence.config.SentenceConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.contentOf;
import static pl.sentence.config.FormatOut.*;

public class SentenceProcessorTest {
    private static final Logger logger = LogManager.getLogger(SentenceProcessorTest.class);
    private SentenceConfig config;

    private static final String INPUT_FILE = "small.in";
    private String path;

    @BeforeEach
    public void init() {
        this.config = new SentenceConfig();
        this.config.setLanguageConfig(LanguageConfig.US);
        ClassLoader classLoader = getClass().getClassLoader();
        this.path = classLoader.getResource("").getFile();
    }


    @Test
    @DisplayName("Should return file the same as small.csv")
    public void processFileToCsvTest() throws IOException, SentenceParserException {
        logger.info("should convert text to sentences in csv format");
        //given
        config.setFormatOut(CSV);
        File expectedResultFile = new File(path + "/small.csv");

        //when
        File actualFile = processFileForTest("csvResult.csv");

        //then
        assertThat(actualFile).exists().isFile();
        assertThat(contentOf(actualFile)).isEqualTo(contentOf(expectedResultFile));
    }

    @Test
    @DisplayName("Should return file the same as small.csv")
    public void processFileToFlatXMLTest() throws SentenceParserException, IOException {
        logger.info("should convert text to sentences in xml flat format");
        //given
        config.setFormatOut(XML_FLAT);
        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("").getFile();

        File expectedResultFile = new File(path + "/small.xml");

        //when
        File actualFile = processFileForTest("xml_flatResult.xml");

        //then
        assertThat(actualFile).exists().isFile();
        assertThat(contentOf(actualFile)).isEqualTo(contentOf(expectedResultFile));
    }

    @Test
    @DisplayName("Should return file the same as small.csv")
    public void processFileToXMLTest() throws SentenceParserException, IOException {
        logger.info("should convert text to sentences in xml pretty format");
        //given
        config.setFormatOut(XML);
        File expectedResultFile = new File(path + "/small_pretty.xml");

        //when
        File actualFile = processFileForTest("xmlResult.xml");

        //then
        assertThat(actualFile).exists().isFile();
        assertThat(contentOf(actualFile)).isEqualToIgnoringWhitespace(contentOf(expectedResultFile));
    }

    private File processFileForTest(String resultFile) throws IOException, SentenceParserException {
        File file = new File(path + "/" + resultFile);
        try (FileInputStream is = new FileInputStream(path + "/"+INPUT_FILE);
             FileOutputStream fop = new FileOutputStream(file, false)) {
            SentenceProcessor sentenceProcessor = new SentenceProcessor(is, fop, config);
            sentenceProcessor.process();
        }
        return file;
    }
}
