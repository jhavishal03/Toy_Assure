package com.increff.Dao;

import com.increff.Constants.UserType;
import com.increff.Pojo.UserPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
@Transactional
public class UserDao extends AbstractDao {
    private static String selectUserById = "select u from UserPojo u where userId=:id";
    
    private static String selectUserByNameAndType = "select u from UserPojo u where name=:name and type=:type";
    
    
    public UserPojo addUser(UserPojo userPojo) {
        em.persist(userPojo);
        return userPojo;
    }
    
    public Optional<UserPojo> getUserByNameAndType(String name, UserType type) {
        TypedQuery<UserPojo> query = getQuery(selectUserByNameAndType, UserPojo.class);
        query.setParameter("name", name);
        query.setParameter("type", type);
        return query.getResultList().stream().findFirst();
    }
    
    public UserPojo findUserById(Long id) {
        TypedQuery<UserPojo> query = getQuery(selectUserById, UserPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }
    
}
