package dev.jzisc.personal.studysrs.exception;

public class DuplicatedDataException extends RuntimeException{

    public DuplicatedDataException(){
        super();
    }

    public DuplicatedDataException(String message) {
        super(message);
    }
}
