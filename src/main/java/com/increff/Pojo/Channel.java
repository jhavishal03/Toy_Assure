package com.increff.Pojo;

import com.increff.Constants.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "assure_Channel")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Channel extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;
    @Column(nullable = false, unique = true)
    private String name;
    @Enumerated(value = EnumType.STRING)
    private InvoiceType invoiceType;
    
}
