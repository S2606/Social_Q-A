package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserDao userDao;

    //Endpoint for creating question
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        try {
            final QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setUuid(UUID.randomUUID().toString());
            questionEntity.setContent(questionRequest.getContent());
            questionEntity.setDate(ZonedDateTime.now());

            final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity, accessToken);
            QuestionResponse question = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
            return new ResponseEntity<QuestionResponse>(question, HttpStatus.CREATED);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    //Endpoint for extracting all questions
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public  ResponseEntity getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        try {
            final List<QuestionEntity> questionEntity = questionService.getAllQuestions(accessToken);
            List<QuestionDetailsResponse> questionResponse = new ArrayList<>();
            for (QuestionEntity q : questionEntity) {
                questionResponse.add(new QuestionDetailsResponse()
                        .content(q.getContent())
                        .id(q.getUuid()));
            }
            return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponse, HttpStatus.OK);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }

    }

    //Endpoint for all questions by userid
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getQuestionbyUserId(@PathVariable("userId") final String userId,
                                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        try {
            final List<QuestionEntity> questionEntity = questionService.getQuestionsByUserUUID(userId, accessToken);
            List<QuestionDetailsResponse> questionResponse = new ArrayList<>();
            for (QuestionEntity q : questionEntity) {
                questionResponse.add(new QuestionDetailsResponse()
                        .content(q.getContent())
                        .id(q.getUuid()));
            }
            return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponse, HttpStatus.OK);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
    //Endpoint for editing the questionid
    @RequestMapping(method = RequestMethod.PUT, path="/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity editQuestion(@PathVariable("questionId") final String questionId, QuestionEditRequest questionEditRequest,
                                                            @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        try {
            final QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setContent(questionEditRequest.getContent());
            questionEntity.setUuid(questionId);


            final QuestionEntity updatedQuestionEntity = questionService.editQuestionContent(questionEntity, accessToken);
            QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");
            return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (InvalidQuestionException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
    //Method for deleting the  question
    @RequestMapping(method = RequestMethod.DELETE, path="/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteQuestion(@PathVariable("questionId") final String questionId,
                                                                 @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        try {
            final QuestionEntity questionEntity = questionService.deleteQuestion(questionId, accessToken);
            QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");
            return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (InvalidQuestionException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
