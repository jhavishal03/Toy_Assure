package com.increff.Dao;

import com.increff.Model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
public class UserDao extends AbstractDao {
    private static String selectById = "select u from User u where userId=:id";


    @Transactional
    public User addUser(User user) {

        em.persist(user);
        return user;
    }

    public User findUserById(Long id) {
        TypedQuery<User> query = getQuery(selectById, User.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

}
