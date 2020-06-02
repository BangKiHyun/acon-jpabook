package jpabook;

import jpabook.entity.Item;
import jpabook.entity.Member;
import jpabook.entity.Order;
import jpabook.entity.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            logic(em);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(EntityManager em) {
        Order findOrder = em.find(Order.class, "찾고 싶은 orderId");
        Member member = findOrder.getMember(); //주문한 회원, 참조 사용

        //주문한 상품 하나를 객체 그래프 탐색하는 방법
        OrderItem orderItem = findOrder.getOrderItems().get(0);
        Item item = orderItem.getItem();
    }
}
