package com.increff.Pojo;

import com.increff.Constants.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "assure_Order", uniqueConstraints = @UniqueConstraint(
        columnNames = {"channelOrderId"}
))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderPojo extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(nullable = false)
    private Long clientId;
    @Column(nullable = false)
    private Long customerId;
    @Column(nullable = false)
    private Long channelId;
    
    @Column(unique = true, nullable = false)
    private String channelOrderId;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    
}

