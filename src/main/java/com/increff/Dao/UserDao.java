package com.increff.Dao;

import com.increff.Constants.UserType;
import com.increff.Model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public class UserDao extends AbstractDao {
    private static String selectUserById = "select u from User u where userId=:id";

    private static String selectUserByNameAndType = "select u from User u where name=:name and type=:type";


    @Transactional
    public User addUser(User user) {

        em.persist(user);
        return user;
    }

    public Optional<User> getUserByNameAndType(String name, UserType type) {
        TypedQuery<User> query = getQuery(selectUserByNameAndType, User.class);
        query.setParameter("name", name);
        query.setParameter("type", type);
        return query.getResultList().stream().findFirst();
    }

    public User findUserById(Long id) {
        TypedQuery<User> query = getQuery(selectUserById, User.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

}
