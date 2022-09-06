package com.increff.Model;

import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class ProductForm implements Serializable {
    @CsvBindByPosition(position = 0, required = true)
    private String clientSkuId;
    @CsvBindByPosition(position = 1, required = true)
    private String name;
    @CsvBindByPosition(position = 2, required = true)
    private String brandId;
    @CsvBindByPosition(position = 3, required = true)
    private double mrp;
    @CsvBindByPosition(position = 4, required = true)
    private String description;
    
    public ProductForm() {
    }
    
    public ProductForm(String clientSkuId, String name, String brandId, double mrp, String description) {
        this.clientSkuId = clientSkuId;
        this.name = name;
        this.brandId = brandId;
        this.mrp = mrp;
        this.description = description;
    }
    
    public String getClientSkuId() {
        return clientSkuId;
    }
    
    public void setClientSkuId(String clientSkuId) {
        this.clientSkuId = clientSkuId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrandId() {
        return brandId;
    }
    
    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }
    
    public double getMrp() {
        return mrp;
    }
    
    public void setMrp(double mrp) {
        this.mrp = mrp;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "ProductDto{" +
                "clientSkuId='" + clientSkuId + '\'' +
                ", name='" + name + '\'' +
                ", brandId='" + brandId + '\'' +
                ", mrp=" + mrp +
                ", description='" + description + '\'' +
                '}';
    }
}
