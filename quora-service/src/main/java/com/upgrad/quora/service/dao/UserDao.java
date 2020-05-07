package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Method to createUser
    public UserEntity createUser(UserEntity userEntity)
    {
        entityManager.persist(userEntity);
        return userEntity;
    }

    //Method to get user by name
    public UserEntity getUserByName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username",userName).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get User by user Id
    public UserEntity getUserByUserId(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get user by Email
    public UserEntity getUserByEmail(final String email){
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to create Auth Token
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    //Method to update user details
    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    //Method to get User Auth token
    public UserAuthTokenEntity getUserAuthToken(final String accessToken)
    {
        try
        {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to validate whether user has signed out or not
    public UserAuthTokenEntity getUserHasSignedOut(final UserEntity user)
    {
        try
        {
            return entityManager.createNamedQuery("userSignedOut",UserAuthTokenEntity.class).setParameter("user",user).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to validate whether user has signed in
    public  UserAuthTokenEntity getUserHasSignedIn(final UserEntity user)
    {
        try
        {
            return entityManager.createNamedQuery("userSignedIn",UserAuthTokenEntity.class).setParameter("user",user).setFirstResult(0).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to signout
    public UserAuthTokenEntity updateSingOut(final ZonedDateTime logOutTime, final String accessToken)
    {
        try
        {
            int num =  entityManager.createQuery("UPDATE UserAuthTokenEntity ut SET ut.logoutAt = :logoutAt WHERE ut.accessToken =:accessToken").setParameter("accessToken",accessToken).setParameter("logoutAt",logOutTime).executeUpdate();

            if(num == 0)
                throw null;
            else
                return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to delete the user
    public UserEntity deleteUser(final String uuid)
    {
        try
        {
            UserEntity userEntity = getUserByUserId(uuid);
            int num =  entityManager.createQuery("DELETE FROM UserEntity ut WHERE ut.uuid =:uuid").setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return userEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Based on AccessToken get the relvant UserId
    public UserEntity getUserByAccessToken(final String accessToken){
        try {
            UserAuthTokenEntity userAuthTokenEntity =  entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
            if(userAuthTokenEntity == null)
            {
                throw  null;
            }
            else
            {
                int id = userAuthTokenEntity.getUser().getId();
                UserEntity userEntity = entityManager.createNamedQuery("userById", UserEntity.class).setParameter("id", id).getSingleResult();
                if(userEntity == null)
                    throw  null;
                else
                    return userEntity;
            }

        }
        catch (NoResultException nre)
        {
            return null;
        }
    }


}
