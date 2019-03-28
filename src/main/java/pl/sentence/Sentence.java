package pl.sentence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Sentence {

    @XmlElement(name = "word")
    private List<String> words;

    @XmlTransient
    private int id;

    @XmlTransient
    private String toString;

    private Sentence() {
    }

    public Sentence(List<String> words, int id) {
        this.id = id;
        this.words = Collections.unmodifiableList(words);
        this.toString = id + ", " + String.join(", ", this.words);
    }

    public List<String> getWords() {
        return words;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        return id == sentence.id &&
                Objects.equals(words, sentence.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(words, id);
    }
}