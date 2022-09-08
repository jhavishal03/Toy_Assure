package com.increff.Pojo;

import com.increff.Constants.UserType;
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
    @Column(nullable = false)
    private String name;
    @Enumerated(value = EnumType.STRING)
    private UserType type;
    
}
