package pl.sentence;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.sentence.config.FormatOut;
import pl.sentence.config.SentenceConfig;
import pl.sentence.writers.CSVWriter;
import pl.sentence.writers.XMLWriter;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.sentence.config.FormatOut.*;

public class SentenceProcessor {
    private final Logger logger = LogManager.getLogger(this.getClass());
    public static final String XML_HEADER = "1.0\" encoding=\"UTF-8\" standalone=\"yes";
    public static final String WORD_CSV_HEADER_ELEMENT = "Word ";
    private static final String TEMP_CSV = ".csvResult.tmp";
    private static final String TEXT_TAG = "text";
    private static final String SENTENCE_PREFIX = "Sentence ";

    private OutputStream outputStream;
    private Reader reader;
    private SentenceConfig config;


    private String csvHeader;


    public SentenceProcessor(InputStream inputStream, OutputStream outputStream, SentenceConfig config) {
        this.outputStream = outputStream;
        this.config = config;
        logger.debug("Configuration : {}", config.toString());
        this.reader = new InputStreamReader(inputStream);
    }

    public void process() throws SentenceParserException {
        logger.info("The processing process has started");
        FormatOut fo = config.getFormatOut();
        if (CSV.equals(fo)) {
            saveToCsvCache();
            addLineToTopOfCSVFile();
        } else {
            try {
                sendXml(fo);
            } catch (XMLStreamException e) {
                throw new SentenceParserException(e);
            }
        }
    }


    private void saveToCsvCache() throws SentenceParserException {
        try (BufferedReader br = new BufferedReader(reader);
             FileWriter fw = new FileWriter(TEMP_CSV, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter outCsv = new PrintWriter(bw)) {

            SentenceWriter saveSentence = new CSVWriter(outCsv);
            int numberOfWords = getSentenceConverter(br, saveSentence);
            outCsv.flush();
            csvHeader = ", " + IntStream.range(1, numberOfWords + 1).mapToObj(i -> WORD_CSV_HEADER_ELEMENT + i).collect(Collectors.joining(", "));

        } catch (IOException | JAXBException | XMLStreamException e) {
            throw new SentenceParserException(e);
        }

    }

    private int getSentenceConverter(BufferedReader br, SentenceWriter saveSentence) throws IOException, JAXBException, XMLStreamException {
        SentenceConverter sentenceConverter = new SentenceConverter(saveSentence, config);
        String line;
        while ((line = br.readLine()) != null) {
            sentenceConverter.convertLineToSentence(line);
        }

        return sentenceConverter.convertLineToSentence(null);
    }


    private void sendXml(FormatOut formatOutput) throws SentenceParserException, XMLStreamException {

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        xof.setProperty("escapeCharacters", false);
        XMLStreamWriter xsw = null;
        try (BufferedReader br = new BufferedReader(reader)) {
            xsw = xof.createXMLStreamWriter(outputStream, "UTF-8");
            if (XML.equals(formatOutput)) {
                xsw = new IndentingXMLStreamWriter(xsw);
            }

            xsw.writeStartDocument(XML_HEADER);

            if (XML_FLAT.equals(formatOutput)) {
                xsw.writeCharacters(config.getEolXml());
            }
            xsw.writeStartElement(TEXT_TAG);
            if (XML_FLAT.equals(formatOutput)) {
                xsw.writeCharacters(config.getEolXml());
            }

            SentenceWriter xmlSender = new XMLWriter(xsw, config);
            getSentenceConverter(br, xmlSender);
            if (XML.equals(formatOutput)) {
                xsw.writeCharacters(config.getEolXml());
            }
            xsw.writeEndDocument();
            xsw.close();

        } catch (IOException | JAXBException | XMLStreamException e) {
            throw new SentenceParserException(e);
        } finally {
            if (xsw != null) {
                xsw.close();
            }
        }
    }


    private void addLineToTopOfCSVFile() throws SentenceParserException {

        try (BufferedReader br = new BufferedReader(new FileReader(TEMP_CSV))) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            ) {
                bw.write(csvHeader);
                bw.write(config.getEolCsv());
                String line;
                while ((line = br.readLine()) != null) {
                    bw.write(SENTENCE_PREFIX + line);
                    bw.write(config.getEolCsv());
                }
                bw.flush();
            }
        } catch (IOException e) {
            throw new SentenceParserException(e);
        }

        try {
            Files.deleteIfExists(Paths.get(TEMP_CSV));
        } catch (IOException e) {
            throw new SentenceParserException(e);
        }
    }
}

