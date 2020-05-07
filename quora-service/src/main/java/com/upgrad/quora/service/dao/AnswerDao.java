package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Method used to create Answer
    public AnswerEntity createAnswer(AnswerEntity answerEntity)
    {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    //Method to get Answer to question
    public AnswerEntity getAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("getByQuestionId", AnswerEntity.class).setParameter("question_id", questionId).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get the Answer by UUID
    public AnswerEntity getAnswerById(final String uuid)
    {
        try
        {
            return entityManager.createNamedQuery("getAnswerById",AnswerEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to update the answer
    public AnswerEntity updateAnswer(final String content, final String uuid, final UserEntity userEntity)
    {
        try
        {
            int num =  entityManager.createQuery("UPDATE AnswerEntity a SET a.ans = :ans WHERE a.uuid =:uuid").setParameter("ans",content).setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return getAnswerByUserIdAndQuestionId(userEntity,uuid);
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get the Answer to Quetion by UserId
    public AnswerEntity getAnswerByUserIdAndQuestionId(final UserEntity userEntity,final String uuid)
    {
        try
        {
            return entityManager.createNamedQuery("getAnswerByUserIdAndQuestionId",AnswerEntity.class).setParameter("user",userEntity).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to delete the answer
    public AnswerEntity deleteAnswer(final String answerId)
    {
        try
        {
            AnswerEntity answerEntity = getAnswerById(answerId);
            int num =  entityManager.createQuery("DELETE FROM AnswerEntity at WHERE at.uuid =:uuid").setParameter("uuid",answerId).executeUpdate();

            if(num == 0)
                throw null;
            else
                return answerEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get the Answers to questionBy ID
    public List<AnswerEntity> getAnswersToQuestionById(final QuestionEntity questionEntity) {
        try {
            List<AnswerEntity> result =  entityManager.createNamedQuery("getAllAnswerToQuestionId", AnswerEntity.class).setParameter("question",questionEntity).getResultList();
            return  result;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }
}
