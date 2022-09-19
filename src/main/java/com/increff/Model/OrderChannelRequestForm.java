package com.increff.Model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderChannelRequestForm {
    @NotEmpty
    private String channelName;
    @NotNull
    @Min(value = 0, message = "client Id should be greater Than 0")
    private Long clientId;
    @NotNull
    private Long customerId;
    @NotEmpty
    private String channelOrderId;
    @NotNull
    private List<OrderItemForm> orderItems;
}
