package com.increff.Util;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.Status;
import com.increff.Constants.UserType;
import com.increff.Dto.ChannelDto;
import com.increff.Dto.OrderItemCsvDto;
import com.increff.Dto.UserDto;
import com.increff.Model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestUtil {

    public static User getUserClient() {
        User user = User.builder().userId(1l).name("MockUser").type(UserType.CLIENT).build();
        return user;
    }

    public static User getUserCustomer() {
        User user = User.builder().userId(2l).name("MockUser2").type(UserType.CUSTOMER).build();
        return user;
    }

    public static UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("MockUser");
        userDto.setType(UserType.CLIENT);
        return userDto;
    }

    public static ChannelDto getChannelDto() {
        ChannelDto channelDto = new ChannelDto();
        channelDto.setName("MockName");
        channelDto.setInvoiceType(InvoiceType.CHANNEL);
        return channelDto;
    }

    public static Channel getChannel() {
        return Channel.builder().channelId(1L).name("MockName").invoiceType(InvoiceType.SELF).build();
    }

    public static List<ChannelListing> getChannelListingList() {
        List<ChannelListing> channelListings = new ArrayList<>();
        channelListings.add(ChannelListing.builder().channelListingId(1L).globalSkuId(101L).channelSkuId("Sku01").
                clientId(1L).channelId(51L).build());
        channelListings.add(ChannelListing.builder().channelListingId(2L).globalSkuId(102L).channelSkuId("Sku02").
                clientId(1L).channelId(51L).build());
        return channelListings;
    }

    public static List<String> getClientSkuIdsList() {
        return Arrays.asList(new String[]{"sku01", "sku02", "sku03"});
    }

    public static List<Product> getProductList() {
        List<Product> res = new ArrayList<>();
        res.add(Product.builder().globalSkuId(101L).clientSkuId("sku01")
                .clientId(1L).brandId("brd01").name("Moq01").mrp(248.90).description("good").build());
        res.add(Product.builder().globalSkuId(102L).clientSkuId("sku02")
                .clientId(2L).brandId("brd02").name("Moq02").mrp(882.90).description("good").build());
        return res;
    }

    public static List<Long> getAllIds() {
        return Arrays.asList(new Long[]{1L, 2L, 3L, 101L, 102l});
    }

    public static List<BinSku> getBinSkuDataList() {
        List<BinSku> res = new ArrayList<>();
        res.add(BinSku.builder().binId(1L).binSkuId(1L).globalSkuId(101L)
                .quantity(5l).build());
        res.add(BinSku.builder().binId(2L).binSkuId(2L).globalSkuId(102L)
                .quantity(15l).build());
        res.add(BinSku.builder().binId(2L).binSkuId(1L).globalSkuId(101L)
                .quantity(6l).build());
        return res;
    }

    public static List<Inventory> getInventoryDataList() {
        List<Inventory> inventories = new ArrayList<>();
        inventories.add(Inventory.builder().inventoryId(1L).allocatedQuantity(10L)
                .availableQuantity(20L).globalSkuId(101L).fulfilledQuantity(0L).build());
        inventories.add(Inventory.builder().inventoryId(2L).allocatedQuantity(0L)
                .availableQuantity(3L).globalSkuId(102L).fulfilledQuantity(1L).build());

        inventories.add(Inventory.builder().inventoryId(1L).allocatedQuantity(10L)
                .availableQuantity(2L).globalSkuId(101L).fulfilledQuantity(0L).build());
        return inventories;
    }


    public static List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder().orderId(1L).channelOrderId("chn01").clientId(1L).channelId(1L).
                customerId(2l).status(Status.CREATED).build());
        orders.add(Order.builder().orderId(2L).channelOrderId("chn02").clientId(1L).channelId(1L).
                customerId(2l).status(Status.ALLOCATED).build());

        return orders;
    }

    public static List<OrderItem> getOrderItems() {
        List<OrderItem> orders = new ArrayList<>();
        orders.add(OrderItem.builder().orderItemId(51L).orderId(1L).sellingPricePerUnit(200.0).globalSkuId(101L)
                .orderedQuantity(5L).fulfilledQuantity(0L).allocatedQuantity(0L).build());

        orders.add(OrderItem.builder().orderItemId(51L).orderId(1L).sellingPricePerUnit(200.0).globalSkuId(101L)
                .orderedQuantity(1L).fulfilledQuantity(0L).allocatedQuantity(5L).build());
        return orders;
    }

    public static List<OrderItemCsvDto> getOrderItemCsvDto() {
        List<OrderItemCsvDto> order = new ArrayList<>();
        OrderItemCsvDto obj = new OrderItemCsvDto();
        obj.setSellingPricePerUnit(122.0);
        obj.setClientSkuId("sku01");
        obj.setOrderedQuantity(5l);
        order.add(obj);
        return order;


    }
}
