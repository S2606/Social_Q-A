package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    // This method is used to create answer for the question
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to post an answer");
        }
        else
        {
            answerEntity.setUser(userAuthTokenEntity.getUser());
            QuestionEntity questionEntity  = questionDao.getQuestionById(questionId);
            if(questionEntity == null)
            {
                throw new InvalidQuestionException("QUES-001","The question entered is invalid");
            }
            else
                answerEntity.setQuestion(questionEntity);

            return answerDao.createAnswer(answerEntity);
        }
    }

    //This method is used to update the answer for the question
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity answerEditedEntity,final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to edit the question");
        }
        else
        {
            AnswerEntity answerEntityResult = answerDao.getAnswerById(answerEditedEntity.getUuid());
            if(answerEntityResult == null)
            {
                throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
            }
            else if(answerEntityResult.getUser() != userAuthTokenEntity.getUser())
            {
                throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
            }
            else
            {
                return answerDao.updateAnswer(answerEditedEntity.getAns(),answerEditedEntity.getUuid(),answerEntityResult.getUser());
            }
        }
    }

    //This method is used to delete the question
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final  String answerId, final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException, AuthenticationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthenticationFailedException("ATHR-002","User is signed out. Sign in first to delete an answer");
        }
        else
        {
            AnswerEntity answerEntityResult = answerDao.getAnswerById(answerId);
            if(answerEntityResult == null)
            {
                throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
            }
            else if(answerEntityResult.getUser() != userAuthTokenEntity.getUser() || !answerEntityResult.getUser().getRole().equals("admin"))
            {
                throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
            }
            else
            {
                return answerDao.deleteAnswer(answerId);
            }
        }
    }

    //This method is used to get all the answers by QuestionId
    public List<AnswerEntity> getAllAnswersByQuestionId(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to get the answers");
        }
        else
        {
            QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
            if(questionEntity == null)
            {
                throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
            }
            else
            {
                List<AnswerEntity> answerEntityList =  answerDao.getAnswersToQuestionById(questionEntity);
                if(answerEntityList.isEmpty())
                {
                    throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
                }
                else
                    return answerEntityList;
            }
        }
    }
}
