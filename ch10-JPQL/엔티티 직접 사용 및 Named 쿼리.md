# 엔티티 직접 사용 및 Named 쿼리

## 엔티티 직접 사용

### 기본 키 값

객체 인스턴스는 찹조 값으로 식별한다.
따라서 JQPL에서 **엔티티 객체를 직접 사용하면 SQL에서는 해당 엔티티의 기본 키 값을 사용**한다.

~~~
select count(m.id) from Member m //엔티티의 아이디를 사용
select count(m) from Member m//엔티티를 직접 사용
~~~

해당 엔티티의 기본 키를 사용하기 때문에 실제 실행된 SQL은 둘 다 다음과 같다.

~~~
select count(m.id) as cnt
from Member m
~~~

</br >

### 외래 키 값

외래 키 또한 엔티티 객체를 직접 사용하면 SQL에서는 해당 엔티티의 외래 키 값을 사용한다.

~~~
Team team = em.find(Team.class, 1L);

String qlString = "select m from Member m where m.team = :team";
List resultList = em.createQuery(qlString)
	.setParameter("team", team)
	.getResultList();
~~~

기본 키 값이 1L인 팀 엔티티를 파라미터로 직접 사용하고 있다. m.team은 현재 team_id라는 외래 키와 매핑되어 있다.
다음과 같은 SQL이 실행된다.

~~~
seelct m.*
from Member m
where m.team_id=?(팀 파라미터의 ID 값)
~~~

참고로 아래와 같이 식별자 값을 직접 사용해도 결과는 같다.

~~~
String qlString = "select m from Member m where m.team.id = :teamId"
~~~

m.team.id를 보면 Member와 Team간에 묵시적 조인이 일어날 것 같지만 Member테이블이 team_id 외래 키를 갖고 있으므로 묵시적 조인은 일어나지 않는다.
물론 m.team.name을 호출하면 묵시적 조인이 일어난다.

</br >

## Named 쿼리: 정적 쿼리

JPQL쿼리는 크게 동적 쿼리와 정적 쿼리로 나뉜다.

- 동적 쿼리: em.createQuery("select ..")처럼 JPQL을 문자로 완성해 직접 넘기는 것
- 정적 쿼리(Named 쿼리): 미리 정의한 쿼리에 이름을 부여해서 필요할 때 사용하는 것
  Named 쿼리는 한 번 정의하면 변경할 수 없는 정적 쿼리다.

### Named 쿼리 장점

- Named 쿼리는 애플리케이션 로딩 시점에 JPQL문법을 체크하고 미리 파싱해두기 떄문에 오류를 빨리 확인할 수 있다.
- 사용하는 시점에는 파싱된 결과를 재사용하므로 성능상 이점이 있다.
- 변하지 않는 정적 SQL이 생성되므로 데이터베이스의 조회 성능 최적화에 도움이 된다.

</br >

### Named 쿼리 어노테이션 정의

Named 쿼리는 이름 그대로 이름을 부여해서 사용하는 방법이다.
@NamedQuery 어노테이션을 통해 Named쿼리를 정의할 수 있다.

~~~
@Entity
@NamedQuery(
	name = "Member.findByUsername",
	query = "select m from Member m where m.username = :username")
public class Member {
	...
}
~~~

- @NamedQuery.name: 쿼리 이름을 부여
- @NamedQuery.query: 사용할 쿼리 입력

~~~
List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
	.setParameter("username" "회원1")
	.getResultList();
~~~

위와 같이 em.createQNamedQuery() 메서드를 이용해서 사용할 수 있다.

### Named 쿼리 이름 부여시 엔티티 이름을 앞에 붙여주자!

- 기능적으로 특별한 의미가 있는 것은 아니지만,
  Named 쿼리는 영속성 유닛 단위로 관리되므로 충돌을 방지하기 위해 엔티티 이름을 앞에 붙여주는게 좋다.
- 엔티티 이름이 앞에 있으면 관리하기 쉽다.
- 예: **Member**.findByUsername

</br >

### 하나의 엔티티에 2개 이상의 Named 쿼리를 정의

@NamedQueries 어노테이션을 사용하면 된다.

~~~
@Entity
@NamedQueries({
	@NamedQuery(
		name = "Member.findByUsername",
		query = "select m from Member m where m.username = :username"),
	@NamedQuery(
		name = "Member.count"
		query = "select count(m) from Member m")
})
public class Member {
	...
}
~~~

</br >

### @NamedQuery 어노테이션 내용

~~~
@Target({TYPE})
public @interface NamedQuery {

	String name(); //Named 쿼리 이름(필수)
	String query(); //JPQL 정의(필수)
	LockModeType lockMode() default NONE; //쿼리 실행 시 락모 설정 가능
	
	QueryHint[] hints() default {}; //JPA 구현체에 쿼리 힌트를 줄 수 있다. 2차 캐시를 다룰 때 사용
}
~~~

</br >

### XML을 사용한 Named쿼리 정의

Named 쿼리를 작성할 때 XML을 사용하는 것이 더 편리하다.

~~~
//ormMember.xml 파일
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">

    <named-query name="Member.findByUsername">
        <query>
            select m
            from Member m
            where m.username = "username
        </query>
    </named-query>

    <named-query name="Member.count">
        <query>select count(m) from Member m</query>
    </named-query>

</entity-mappings>
~~~

위와 같이 정의한 xml을 인식하도록 META-INF/persistence.xml에 다음 코드를 추가하자

~~~
<persistence-unit name="jpabook" >
	<mapping-file>META-INF/ormMember.xml</mapping-file>
	...
~~~

### 참고

META-INF/orm.xml은 JPA가 기본 매핑파일로 인식해서 별도의 설정이 필요 없다. 이름이나 위치가 다르면 설정을 추가해야한다.
여기서는 매핑 파일 이름이 ormMember.xml이므로 persistence.xml정보에 추가해야한다.

</br >

### 환경에 따른 설정

만약 XML과 어노테이션에 같은 설정이 있으면 XML이 우선이다.
따라서 어플리케이션이 운영 환경에 따라 다른 쿼리를 실행해야 한다면 각 환경에 맞춘 XML을 준비해 두고 XML만 변경해서 배포하면 된다.