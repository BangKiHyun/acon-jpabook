package jpabook.jpashop.domain;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;

public class OrderSpec {

    public static Specification<Order> memberNameLike(final String memberName) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(final Root<Order> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
                if (StringUtils.isEmpty(memberName)) {
                    return null;
                }

                Join<Order, Member> m =
                        root.join("member", JoinType.INNER); //회원과 조인
                return cb.like(m.<String>get("name"),
                        "%" + memberName + "%");
            }
        };
    }

    public static Specification<Order> orderStatusEq(final OrderStatus orderStatus) {
        return new Specification<Order>() {
            @Override
            public Predicate toPredicate(final Root<Order> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {

                if (orderStatus == null) {
                    return null;
                }

                return cb.equal(root.get("status"), orderStatus);
            }
        };
    }
}
