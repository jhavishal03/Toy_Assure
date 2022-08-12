package com.increff.Model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

@MappedSuperclass
@Builder
@RequiredArgsConstructor
@Data
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

