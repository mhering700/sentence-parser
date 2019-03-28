package pl.sentence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.sentence.config.LanguageConfig;
import pl.sentence.config.SentenceConfig;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.sentence.config.FormatOut.CSV;
import static pl.sentence.config.FormatOut.XML;

public class SentenceConverterTest {
    private static final Logger logger = LogManager.getLogger(SentenceConverterTest.class);
    private SentenceConfig config;

    @BeforeEach
    public void init() {
        config = new SentenceConfig();
        config.setLanguageConfig(LanguageConfig.US);
    }

    @Test
    @DisplayName("should convert one line to two sentences for csv")
    public void shouldConvertOneLineToTwoSentencesTest() throws JAXBException, XMLStreamException {
        logger.info("should convert one line to two sentences for csv");
        //given
        config.setFormatOut(CSV);
        String oneLine = "Mr. and () Ms. Smith you’d ,  met Dr. Jekyll ,, outside. What the \n shouted was, He, he ";
        List<String> wordsList1 = Arrays.asList("and", "Dr.", "Jekyll", "met", "Mr.", "Ms.", "outside", "Smith", "you'd");
        List<String> wordsList2 = Arrays.asList("he", "He", "shouted", "the", "was", "What");
        List<Sentence> expectedListOfSentences = new ArrayList<>();
        expectedListOfSentences.add(new Sentence(wordsList1, 1));
        expectedListOfSentences.add(new Sentence(wordsList2, 2));

        //when
        List<Sentence> actualList = new ArrayList<>();
        SentenceConverter sentenceConverter = new SentenceConverter((actualList::add), config);
        sentenceConverter.convertLineToSentence(oneLine);
        sentenceConverter.convertLineToSentence(null);

        //then
        assertThat(actualList).usingFieldByFieldElementComparator().containsAll(expectedListOfSentences);
    }

    @Test
    @DisplayName("should convert two line to three sentences for csv")
    public void shouldConvertTwoLineToThreeSentencesTest() throws JAXBException, XMLStreamException {
        logger.info("should convert two line to three sentences for csv");
        //given
        config.setFormatOut(CSV);
        String firstLine = "Mr. and () Ms. Smith ,  met Dr. Jekyll ,, outside. What the \n he shouted was";
        String secondLine = "shocking:  停在那儿, 你这肮脏的掠夺者! I couldn't understand a word,perhaps because Chinese \n" +
                " isn't my mother tongue.";
        List<String> wordsList1 = Arrays.asList("and", "Dr.", "Jekyll", "met", "Mr.", "Ms.", "outside", "Smith");
        List<String> wordsList2 = Arrays.asList("he", "shocking", "shouted", "the", "was", "What", "你这肮脏的掠夺者", "停在那儿");
        List<String> wordsList3 = Arrays.asList("a", "because", "Chinese", "couldn't", "I", "isn't", "mother", "my", "perhaps", "tongue", "understand", "word");
        List<Sentence> expectedListOfSentences = new ArrayList<>();
        expectedListOfSentences.add(new Sentence(wordsList1, 1));
        expectedListOfSentences.add(new Sentence(wordsList2, 2));
        expectedListOfSentences.add(new Sentence(wordsList3, 3));

        //when
        List<Sentence> actualList = new ArrayList<>();
        SentenceConverter sentenceConverter = new SentenceConverter((actualList::add), config);
        sentenceConverter.convertLineToSentence(firstLine);
        sentenceConverter.convertLineToSentence(secondLine);
        sentenceConverter.convertLineToSentence(null);

        //then
        assertThat(actualList).usingFieldByFieldElementComparator().containsAll(expectedListOfSentences);
    }

    @Test
    @DisplayName("should convert two line to three sentences for xml")
    public void shouldConvertTwoLineToThreeSentencesForXmlTest() throws JAXBException, XMLStreamException {
        logger.info("should convert two line to three sentences for xml");
        //given
        config.setFormatOut(XML);
        String firstLine = "Mr. and () Ms. Smith ,  met Dr. Jekyll ,, outside. What the \n he shouted was";
        String secondLine = "shocking:  停在那儿, 你这肮脏的掠夺者! I couldn't understand a word, you’d perhaps because Chinese \n" +
                " isn't my mother tongue.";
        List<String> wordsList1 = Arrays.asList("and", "Dr.", "Jekyll", "met", "Mr.", "Ms.", "outside", "Smith");
        List<String> wordsList2 = Arrays.asList("he", "shocking", "shouted", "the", "was", "What", "你这肮脏的掠夺者", "停在那儿");
        List<String> wordsList3 = Arrays.asList("a", "because", "Chinese", "couldn&apos;t", "I", "isn&apos;t", "mother", "my", "perhaps", "tongue", "understand", "word", "you&apos;d");
        List<Sentence> expectedListOfSentences = new ArrayList<>();
        expectedListOfSentences.add(new Sentence(wordsList1, 1));
        expectedListOfSentences.add(new Sentence(wordsList2, 2));
        expectedListOfSentences.add(new Sentence(wordsList3, 3));

        //when
        List<Sentence> actualList = new ArrayList<>();
        SentenceConverter sentenceConverter = new SentenceConverter((actualList::add), config);
        sentenceConverter.convertLineToSentence(firstLine);
        sentenceConverter.convertLineToSentence(secondLine);
        sentenceConverter.convertLineToSentence(null);

        //then
        assertThat(actualList).usingFieldByFieldElementComparator().containsAll(expectedListOfSentences);
    }
}