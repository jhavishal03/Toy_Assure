package com.increff.Model;

import com.increff.Constants.InvoiceType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "assure_Channel")
@Builder
@RequiredArgsConstructor
@Data
public class Channel extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;
    @NotEmpty
    private String name;
    @Enumerated(value = EnumType.STRING)
    private InvoiceType invoiceType;

}
