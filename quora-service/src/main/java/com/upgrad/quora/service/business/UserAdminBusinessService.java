package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    //This method is used to create new user details
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException
    {
        UserEntity objUserEntityName = userDao.getUserByName(userEntity.getUsername());
        if(objUserEntityName != null)
        {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken.");
        }

        UserEntity objUserEntityEmail = userDao.getUserByEmail(userEntity.getEmail());
        if(objUserEntityEmail != null)
        {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailid.");
        }
        else
        {

            String password = userEntity.getPassword();

            if(password == null)
            {
                password = "quora@123";
            }
            String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
    }

    //This method is used to extract the user details
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException,UserNotFoundException
    {

        UserEntity userEntity = userDao.getUserByUserId(userUuid);
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserHasSignedIn(userEntity);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to get user details");
        }

        return  userEntity;
    }

    //This method is used to delete the user details
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException, AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserId(userUuid);
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        else if(!userEntity.getRole().equals("admin"))
        {
            throw new AuthenticationFailedException("ATHR-003","Unauthorized Access, Entered user is not admin");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserHasSignedIn(userEntity);
        if(userAuthTokenEntity == null)
        {

            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.");
        }

        if(userEntity.getRole().equals("admin"))
        {
            userEntity = userDao.deleteUser(userUuid);
        }

        return  userEntity;
    }


}
