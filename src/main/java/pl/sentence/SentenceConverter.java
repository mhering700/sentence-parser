package pl.sentence;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.sentence.config.SentenceConfig;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.sentence.config.FormatOut.CSV;

class SentenceConverter {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private SentenceConfig config;

    private SentenceWriter sentenceWriter;

    private int idSentence;
    private int numberOfWords;

    private String restOfLine = "";

    SentenceConverter(SentenceWriter sentenceWriter, SentenceConfig config) {
        this.sentenceWriter = sentenceWriter;
        this.config = config;
    }

    int convertLineToSentence(String inputLine) throws JAXBException, XMLStreamException {
        logger.log(Level.TRACE, "inputLine: {}", inputLine);
        boolean isLast = inputLine == null;

        String entireStr = restOfLine + " " + Optional.ofNullable(inputLine).orElse("");

        entireStr = entireStr.replaceAll("\\s+", " ");
        if (entireStr.isEmpty()) {
            summary(isLast);
            return numberOfWords;
        }

        if (entireStr.length() < 50 && !isLast) {
            restOfLine = entireStr;
            return numberOfWords;
        }

        splitToSentence(entireStr);

        if (isLast && !restOfLine.isEmpty()) {
            saveSentence(restOfLine);
        }
        summary(isLast);

        return numberOfWords;
    }


    private void splitToSentence(String input) throws JAXBException, XMLStreamException {
        BreakIterator iterator = BreakIterator.getSentenceInstance(config.getLanguageConfig().getLocale());
        iterator.setText(input);
        int start = iterator.first();
        int sourceLength = input.length();
        StringBuilder sb = new StringBuilder();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String sentence = input.substring(start, end);
            if (end == sourceLength) {
                restOfLine = sentence;
            } else {
                if (hasAbbreviation(sentence)) {
                    sb.append(sentence);
                } else {
                    sentence = sb.append(sentence).toString();
                    saveSentence(sentence);
                    sb = new StringBuilder();
                }
            }
        }
        restOfLine = sb.append(restOfLine).toString();
    }


    private void saveSentence(String str) throws JAXBException, XMLStreamException {

        List<String> words = convertStringToWords(str);
        List<String> wordsAfterEscapeChar = words.stream().map(this::escapeSpecialString).sorted((a, b) -> {
            int compare = a.compareToIgnoreCase(b);
            if (compare == 0) {
                return a.compareTo(b) * -1;
            }
            return compare;
        }).collect(Collectors.toList());
        saveSentence(wordsAfterEscapeChar);
    }

    private List<String> convertStringToWords(String sentenceStr) {
        sentenceStr = sentenceStr.trim().replaceAll("[.!?]$", "").replaceAll("[(){}]", "");
        String[] words = sentenceStr.split("[\\s,(\\s-\\s):;]+");
        return Arrays.asList(words);
    }


    private void saveSentence(List<String> raw) throws JAXBException, XMLStreamException {
        int size = raw.size();
        if (size > numberOfWords) {
            numberOfWords = size;
        }
        idSentence++;
        Sentence sentence = new Sentence(raw, idSentence);
        sentenceWriter.process(sentence);
    }

    private boolean hasAbbreviation(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return false;
        }
        for (String w : config.getLanguageConfig().getAbbreviations()) {
            if (sentence.contains(w)) {
                return true;
            }
        }
        return false;
    }

    private String escapeSpecialString(String str) {
        String forCsv = str.replaceAll("’", "'");
        if (!CSV.equals(config.getFormatOut())) {
            return StringEscapeUtils.escapeXml10(forCsv);
        }
        return StringEscapeUtils.escapeCsv(forCsv);
    }

    private void summary(boolean isLast) {
        if (isLast) logger.info("{} sentences have been saved", idSentence);
    }
}

