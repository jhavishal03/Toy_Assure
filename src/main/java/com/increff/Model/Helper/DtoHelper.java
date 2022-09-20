package com.increff.Model.Helper;

import com.increff.Constants.Constants;
import com.increff.Constants.Status;
import com.increff.Model.UserForm;
import com.increff.Pojo.InventoryPojo;
import com.increff.Pojo.OrderPojo;
import com.increff.Util.StringUtil;
import com.increff.common.Exception.ApiGenericException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DtoHelper extends Constants {
    
    public static void check(UserForm userForm) {
        if (StringUtil.isEmpty(userForm.getName())) {
            throw new ApiGenericException("UserName cannot be empty/null");
        }
        if (Objects.isNull(userForm.getType())) {
            throw new ApiGenericException("UserType " + CANNOT_BE_NULL);
        }
    }
    
    public static void check(Long id, String fieldName) {
        if (Objects.isNull(id)) {
            throw new ApiGenericException(fieldName + CANNOT_BE_NULL);
        }
        if (id < 0l) {
            throw new ApiGenericException(fieldName + GREATER_THAN_ZERO);
        }
    }
    
    public static void check(OrderPojo order) {
        if (order.getStatus().equals(Status.ALLOCATED)) {
            throw new ApiGenericException("Order already Allocated");
        }
        
        if (order.getStatus().equals(Status.FULFILLED)) {
            throw new ApiGenericException("Order already Fulfilled");
        }
    }
    
    public static void checkFulfil(OrderPojo order) {
        if (order.getStatus().equals(Status.CREATED)) {
            throw new ApiGenericException("Order already Created");
        }
        
        if (order.getStatus().equals(Status.FULFILLED)) {
            throw new ApiGenericException("Order already Fulfilled");
        }
    }
    
    public static List<InventoryPojo> convertInventoryFromProductList(List<Long> productPojos) {
        List<InventoryPojo> res = new ArrayList<>();
        for (Long globalSku : productPojos) {
            res.add(InventoryPojo.builder().globalSkuId(globalSku).
                    allocatedQuantity(0l).availableQuantity(0l).fulfilledQuantity(0l).build());
        }
        return res;
    }
}
