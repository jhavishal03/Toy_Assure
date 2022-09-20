package com.increff.Util;

import com.increff.common.Exception.ApiGenericException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.collections.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CSVParseUtil<T> {
    public static <T> List<T> parseCSV(byte[] csvBytes, Class<? extends T> type) {
        CsvToBean<T> csvBean = null;
        List<T> parsedData = new ArrayList<>();
        List<CsvException> errors = null;
        try {
            csvBean = (CsvToBean<T>) new CsvToBeanBuilder<>(new InputStreamReader(new ByteArrayInputStream(csvBytes), "UTF8"))
                    .withType(type).withThrowExceptions(false).withSkipLines(1).build();
            parsedData = csvBean.parse();
            errors = csvBean.getCapturedExceptions();
            if (CollectionUtils.isNotEmpty(errors)) {
                handleErrors(errors);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ApiGenericException("Error while CSV Parsing ");
        }
        return parsedData;
    }
    
    
    private static void handleErrors(List<CsvException> errors) {
        List<String> errorMessages = new ArrayList<>();
        for (CsvException e : errors) {
            String errorMessage = e.getMessage().replaceAll("\\.", "");
            errorMessages.add(errorMessage + " at line number : " + e.getLineNumber());
        }
        throw new ApiGenericException("Error while Csv parsing", errorMessages);
    }
}
