package com.increff.Exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiGenericException extends RuntimeException {
    Object data;
    
    public ApiGenericException(String message) {
        super(message);
    }
    
    public ApiGenericException(String message, Object data) {
        super(message);
        this.data = data;
    }
    
}
