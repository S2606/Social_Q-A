package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.SigninBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    SigninBusinessService signinBusinessService;

    //Endpoint for user Signup
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
            final UserEntity userEntity = new UserEntity();
            userEntity.setUuid(UUID.randomUUID().toString());
            userEntity.setFirstName(signupUserRequest.getFirstName());
            userEntity.setLastName(signupUserRequest.getLastName());
            userEntity.setUsername(signupUserRequest.getUserName());
            userEntity.setEmail(signupUserRequest.getEmailAddress());
            userEntity.setContactnumber(signupUserRequest.getContactNumber());
            userEntity.setPassword(signupUserRequest.getPassword());
            userEntity.setSalt("1234abc");
            userEntity.setCountry(signupUserRequest.getCountry());
            userEntity.setDob(signupUserRequest.getDob());
            userEntity.setAboutme(signupUserRequest.getAboutMe());
            userEntity.setRole("nonadmin");

            final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
            SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
            return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    //Endpoint for user Signin
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {


            String s = authorization.split("Basic ")[1];
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");

            UserAuthTokenEntity userAuthToken = signinBusinessService.authenticate(decodedArray[0], decodedArray[1]);
            UserEntity user = userAuthToken.getUser();

            SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", userAuthToken.getAccessToken());
            return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);

    }

    //Endpoint for user Signout
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity logout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {


            UserAuthTokenEntity userAuthToken = signinBusinessService.singOutUser(accessToken);

            SignoutResponse signoutResponse = new SignoutResponse().id(userAuthToken.getUser().getUuid()).message("SIGNED OUT SUCCESSFULLY");
            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", userAuthToken.getAccessToken());
            return new ResponseEntity<SignoutResponse>(signoutResponse, headers, HttpStatus.OK);

    }
}
