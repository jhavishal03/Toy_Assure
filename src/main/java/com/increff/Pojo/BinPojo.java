package com.increff.Pojo;

import javax.persistence.*;

@Entity
@Table(name = "assure_Bin")
public class BinPojo extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long binId;
    
    
    public BinPojo() {
    }
    
    public Long getBinId() {
        return binId;
    }
    
    public void setBinId(Long binId) {
        this.binId = binId;
    }
}
