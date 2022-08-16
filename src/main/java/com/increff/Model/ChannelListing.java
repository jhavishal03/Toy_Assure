package com.increff.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assure_ChannelListing")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelListing extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelListingId;
    @NotNull
    private Long channelId;
    @NotEmpty
    private String channelSkuId;
    @NotNull
    private Long clientId;
    @Min(0)
    private Long globalSkuId;

}
