package com.increff.Exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApiGenericException extends RuntimeException implements Serializable {
    Object data;
    
    public ApiGenericException(String message) {
        super(message);
    }
    
    public ApiGenericException(String message, Object data) {
        super(message);
        this.data = data;
    }
    
}
