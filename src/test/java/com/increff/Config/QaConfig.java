package com.increff.Config;

import com.increff.spring.SpringConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;


@Configuration
@ComponentScan(//
        basePackages = {"com.increff"}, //
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {SpringConfig.class})//
)
@PropertySources({ //
        @PropertySource(value = "classpath:./com/increff/Test.properties", ignoreResourceNotFound = true) //
})
public class QaConfig {


}