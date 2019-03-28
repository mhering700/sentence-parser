package pl.sentence;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public interface SentenceWriter {
    void process(Sentence sentence) throws JAXBException, XMLStreamException;
}
