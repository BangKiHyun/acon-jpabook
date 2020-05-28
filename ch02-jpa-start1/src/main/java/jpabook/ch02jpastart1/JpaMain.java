package jpabook.ch02jpastart1;

import jpabook.ch02jpastart1.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        // 엔티티 매니저 팩토리 생성
        // 애플리케이션 전체에서 딱 한번만 생성하고 공유해서 사용해야 함!
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("jpabook");

        // 엔티티 매니저 생성
        // 이를 사용해서 엔티티를 DB에 CRUD 할 수 있다.
        // DB 커넥션과 밀접한 관계가 있으므로 스레드간 공유하거나 재사용하면 안된다.
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 API

        try {
            tx.begin(); // 트랜잭션 시작
            logic(em); // 비즈니스 로직 실행
            tx.commit(); // 트랜잭션 커밋
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close(); //사용이 끝난 엔티티 매니저는 반드시 종료해야 한다.
        }
        emf.close(); // 애플리케이션을 종료할 때 엔티티 매니저 팩토리는 종료해야 한다.
    }

    private static void logic(EntityManager em) {

        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("기현");
        member.setAge(14);

        //등록
        em.persist(member);

        //수정
        member.setAge(20);

        //한 건 조회
        Member findMember = em.find(Member.class, id);
        System.out.println("findMember=" + findMember.getUsername()
                + ", age=" + findMember.getAge());

        //JPQL(Java Persistence Query Language)
        List<Member> members =
                em.createQuery("select  m from Member m", Member.class)
                        .getResultList();
        System.out.println("members.size=" + members.size());

        //삭제
        em.remove(member);
    }
}
