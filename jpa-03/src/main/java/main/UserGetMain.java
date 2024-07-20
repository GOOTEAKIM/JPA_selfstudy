package main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.reserve.domain.User;

public class UserGetMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabegin");
        // 영속 단위 기준으로 초기화
        // 필요한 자원 생성
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            User user = entityManager.find(User.class, "user@user.com");
            if (user == null) {
                System.out.println("User 없음");
            } else {
                System.out.printf("User 있음: email=%s, name=%s, createDate=%s\n",
                        user.getEmail(), user.getName(), user.getCreateDate());
            }
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            transaction.rollback();
        } finally {
            entityManager.close();
        }
        emf.close();
    }
}
