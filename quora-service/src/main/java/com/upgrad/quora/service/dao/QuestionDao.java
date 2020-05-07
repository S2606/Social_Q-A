package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * This function is used for database interaction of inserting a new question
     * @return
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity)
    {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     *
     * @return
     */
    public List<QuestionEntity> getAllQuestions() {
        try {
            List<QuestionEntity> result =  entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
            return  result;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     * for a particular question UUID
     *
     * @return
     */
    public QuestionEntity getQuestionById(final String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     * for a particular user UUID
     *
     * @return
     */
    public List<QuestionEntity> getQuestionsByUserUUId(final UserEntity userEntity) {
        try {
            List<QuestionEntity> result =  entityManager.createNamedQuery("getQuestionByUserId", QuestionEntity.class).setParameter("user",userEntity).getResultList();
            return  result;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of updating a question
     *
     * @return
     */
    public QuestionEntity updateQuestion(final String content, final String uuid, final UserEntity userEntity)
    {
        try
        {
            int num =  entityManager.createQuery("UPDATE QuestionEntity q SET q.content = :content WHERE q.uuid =:uuid").setParameter("content",content).setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return getQuestionByUserIdAndQuestionId(userEntity,uuid);
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    //Method to get the question by userid and questionid
    public QuestionEntity getQuestionByUserIdAndQuestionId(final UserEntity userEntity,final String uuid)
    {
        try
        {
            return entityManager.createNamedQuery("getQuestionByUserIdAndQuestionId",QuestionEntity.class).setParameter("user",userEntity).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of deleting a question
     *
     * @param uuid: UUID of Question to be deleted
     * @return
     */
    public QuestionEntity deleteQuestion(final String uuid)
    {
        try
        {
            QuestionEntity questionEntity = getQuestionById(uuid);
            int num =  entityManager.createQuery("DELETE FROM QuestionEntity qt WHERE qt.uuid =:uuid").setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return questionEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }
}
