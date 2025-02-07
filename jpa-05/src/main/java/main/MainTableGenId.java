package main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jpabasic.reserve.domain.AccessLog;
import jpabasic.reserve.jpa.EMF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class MainTableGenId {
    private static Logger logger = LoggerFactory.getLogger(MainTableGenId.class);

    public static void main(String[] args) {
        EMF.init();
        EntityManager em = EMF.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            AccessLog log = new AccessLog("/path01", LocalDateTime.now());
            logger.info("persist 실행 전");
            em.persist(log);
            logger.info("persist 실행 함");
            logger.info("생성한 식별자: {}", log.getId());
            logger.info("커밋하기 전");
            tx.commit();  // 트랜잭션을 한 번만 커밋
            logger.info("커밋함");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();  // 예외 발생 시 롤백
            }
            logger.error("트랜잭션 실패: ", e);
        } finally {
            em.close();
        }
        EMF.close();
    }
}
