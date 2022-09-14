package com.increff.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "assure_ChannelListing", uniqueConstraints = @UniqueConstraint(
        columnNames = {"channelId", "channelSkuId"}
))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelListingPojo extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelListingId;
    @Column(nullable = false)
    private Long channelId;
    @Column(nullable = false)
    private String channelSkuId;
    @Column(nullable = false)
    private Long clientId;
    @Column(nullable = false)
    private Long globalSkuId;
    
}
