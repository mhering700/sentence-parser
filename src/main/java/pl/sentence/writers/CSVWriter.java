package pl.sentence.writers;

import pl.sentence.Sentence;
import pl.sentence.SentenceWriter;

import java.io.PrintWriter;

public class CSVWriter implements SentenceWriter {

    private PrintWriter printWriter;

    public CSVWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    @Override
    public void process(Sentence sentence) {
        printWriter.println(sentence.toString());
    }
}
