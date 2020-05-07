package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {


    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    //Enpoint for extracting userProfile
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException
    {
        try {
            final UserEntity userEntity = userAdminBusinessService.getUser(userUuid, authorization);
            UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                    .firstName(userEntity.getFirstName())
                    .lastName(userEntity.getLastName())
                    .userName(userEntity.getUsername())
                    .emailAddress(userEntity.getEmail())
                    .country(userEntity.getCountry())
                    .contactNumber(userEntity.getContactnumber())
                    .dob(userEntity.getDob())
                    .aboutMe(userEntity.getAboutme());
            return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
