package pl.sentence.writers;

import pl.sentence.Sentence;
import pl.sentence.SentenceWriter;
import pl.sentence.config.SentenceConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static pl.sentence.config.FormatOut.XML_FLAT;

public class XMLWriter implements SentenceWriter {


    private SentenceConfig config;
    private XMLStreamWriter xmlStreamWriter;
    private QName root;

    private Marshaller marshaller;
    private static final Class<Sentence> clazz = Sentence.class;

    public XMLWriter(XMLStreamWriter xmlStreamWriter, SentenceConfig config) throws JAXBException {
        this.config = config;
        this.xmlStreamWriter = xmlStreamWriter;

        JAXBContext jc = JAXBContext.newInstance(Sentence.class);
        marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        root = new QName("sentence");

    }

    @Override
    public void process(Sentence sentence) throws JAXBException, XMLStreamException {

        JAXBElement<Sentence> element = new JAXBElement<>(root, clazz, sentence);
        marshaller.marshal(element, xmlStreamWriter);
        if (XML_FLAT.equals(config.getFormatOut())) {
            xmlStreamWriter.writeCharacters(config.getEolXml());
        }
    }
}
