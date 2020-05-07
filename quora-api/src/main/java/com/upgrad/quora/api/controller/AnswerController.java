package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {

    @Autowired
    AnswerService answerService;

    //Endpoint for creating the answer for a question
    @RequestMapping(method = RequestMethod.POST, path = "/answer/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createAnswer(@PathVariable("questionId") final String questionId, final AnswerRequest answerRequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
            final AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setAns(answerRequest.getAnswer());
            answerEntity.setUuid(UUID.randomUUID().toString());
            answerEntity.setDate(ZonedDateTime.now());

            final AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity, accessToken, questionId);
            AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
            return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //Endpoint for editing the answer
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateAnswer(@PathVariable("answerId") final String answerId, final AnswerEditRequest answerEditRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException
    {
            final AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setAns(answerEditRequest.getContent());
            answerEntity.setUuid(answerId);

            final AnswerEntity updatedAnswerEntity = answerService.updateAnswer(answerEntity, authorization);
            AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
            return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Endpoint for deleting the answer
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException, AuthenticationFailedException {

            final AnswerEntity answerEntity = answerService.deleteAnswer(answerId, authorization);
            AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
            return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);

    }

    //Endpoint for getting all answers to a question
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getAllAnswersToQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

            final List<AnswerEntity> answerDetailsResponses = answerService.getAllAnswersByQuestionId(questionId, accessToken);
            List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<>();
            for (AnswerEntity a : answerDetailsResponses) {
                answerDetailsResponseList.add(new AnswerDetailsResponse()
                        .answerContent(a.getAns())
                        .questionContent(a.getQuestion().getContent())
                        .id(a.getUuid()));
            }
            return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);


    }
}
