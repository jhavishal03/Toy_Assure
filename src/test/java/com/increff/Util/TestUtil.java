package com.increff.Util;

import com.increff.Constants.InvoiceType;
import com.increff.Constants.UserType;
import com.increff.Dto.ChannelDto;
import com.increff.Dto.UserDto;
import com.increff.Model.Channel;
import com.increff.Model.ChannelListing;
import com.increff.Model.Product;
import com.increff.Model.User;

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
}
