package com.increff.Pojo;

import com.increff.Constants.UserType;
import com.increff.Util.ValueOfEnum;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class TestData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ValueOfEnum(enumClass = UserType.class)
    private String type;
}
