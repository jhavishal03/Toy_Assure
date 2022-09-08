package com.increff.Util;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.validators.StringValidator;
import com.opencsv.exceptions.CsvValidationException;

public class MustAValidNumber implements StringValidator {
    
    
    @Override
    public boolean isValid(String value) {
        double num = 0;
        boolean valid = true;
        if (value == null)
            valid = false;
        else {
            try {
                num = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        return valid && num > 0;
    }
    
    @Override
    public void validate(String value, BeanField field) throws CsvValidationException {
        if (!isValid(value)) {
            throw new CsvValidationException("Field '" + field.getField().getName() + "' cannot be negative or zero");
        }
    }
    
    @Override
    public void setParameterString(String value) {
        return;
    }
}

