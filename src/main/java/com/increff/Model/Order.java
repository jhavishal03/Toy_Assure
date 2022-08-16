package com.increff.Model;

import com.increff.Constants.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assure_Order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @NotNull
    private Long clientId;
    @Min(0)
    private Long customerId;
    @Min(0)
    private Long channelId;
    @NotEmpty
    @Column(unique = true)
    private String channelOrderId;
    @Enumerated(value = EnumType.STRING)
    private Status status;

}

