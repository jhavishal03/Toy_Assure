package com.increff.Dto;

import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class ProductDto implements Serializable {
    @CsvBindByPosition(position = 0)
    private String clientSkuId;
    @CsvBindByPosition(position = 1)
    private String name;
    @CsvBindByPosition(position = 2)
    private String brandId;
    @CsvBindByPosition(position = 3)
    private double mrp;
    @CsvBindByPosition(position = 4)
    private String description;

    public ProductDto() {
    }

    public ProductDto(String clientSkuId, String name, String brandId, double mrp, String description) {
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
