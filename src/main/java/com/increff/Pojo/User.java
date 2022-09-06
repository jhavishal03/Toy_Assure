package com.increff.Model;

import com.increff.Constants.UserType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "assure_User")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    private String name;
    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty
    private UserType type;
    
}
