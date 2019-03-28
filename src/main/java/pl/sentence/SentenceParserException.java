package pl.sentence;

public class SentenceParserException extends Exception {

    public SentenceParserException(Throwable cause) {
        super(cause);
    }

    public SentenceParserException(String message) {
        super(message);

    }
}
