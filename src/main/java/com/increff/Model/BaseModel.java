package com.increff.Model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseModel {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @Version
    private Long version;


}

