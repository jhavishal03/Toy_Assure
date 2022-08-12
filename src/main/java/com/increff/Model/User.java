package com.increff.Model;

import com.increff.Constants.UserType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "assure_User")
@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class User extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotEmpty
    private String name;
    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty
    private UserType type;

}
