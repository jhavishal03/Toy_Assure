package com.increff.Model;

import com.increff.Constants.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

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
    @NotEmpty
    private String name;
    @Enumerated(value = EnumType.STRING)
    private InvoiceType invoiceType;

}
