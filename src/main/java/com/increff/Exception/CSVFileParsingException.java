package com.increff.Exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CSVFileParsingException extends RuntimeException implements Serializable {
    Object data;
    
    public CSVFileParsingException(String message) {
        super(message);
    }
    
    public CSVFileParsingException(String message, Object data) {
        super(message);
        this.data = data;
    }
    
}
