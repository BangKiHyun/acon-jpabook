# 네이티브 SQL

## 네이티브 SQL 정의

다양한 이유로 JPQL을 사용할 수 없을 때 JPA는 SQL을 직접 사용할 수 있는 기능을 제공한다. 이를 네이티브 SQL이라 한다.
JPQL을 사용하면 JPA가 SQL을 자동으로 생성해 준다면, 네이티브 SQL은 수동으로 직접 정의하는 것이다.

**네이티브 SQL을 사용하면 엔티티를 조회할 수 있고 JPA가 지원하는 영속성 컨텍스트의 기능을 그대로 사용할 수 있다.**
반면에 JDBC API를 직접 사용하면 단순히 데이터의 나열을 조회할 뿐이다.

</br >

## 네이티브 SQL사용

네이티브 쿼리 API는 다음 3가지가 있다.

- 결과 타입 정의
  public Query createNatieveQuery(String sqlString, Class resultClass);
- 결과 타입을 정의할 수 없을 때
  public Query createNativeQuery(String sqlString);
- 결과 매핑 사용
  public Query createNativeQuery(String sqlString,
  	String resultSetMapping);

</br >

## 엔티티 조회

em.createNativeQuery(SQL, 결과 클래스)를 사용한다.
첫 번째 파라미터: 네이티브 SQL, 두 번째 파라미터: 조회할 엔티티 클래스 타입

~~~
//SQL 정의
String sql = 
	"SELECT ID, AGE, NAME, TEAM_ID " +
	"FROM MEMBER WHERE AGE > ?";
	
Query nativeQuery = em.createNativeQuery(sql, Member.class)
	.setParameter(1, 20);
	
List<Member> resultList = nativeQUery.getResultList();
~~~

네이티브 SQL로 SQL만 직접 사용하고 나머지는 JPQL을 사용할 때와 같다. 조회한 엔티티도 영속성 컨텍스트에서 관리된다.

위 코드를 보면 위치 기반 파라미터를 사용했다. JPA는 공식적으로 네이티브 SQL에 이름 기반 파라미터를 지원하지 않기 때문이다. 그러나 하이버네이티는 네이티브 SQL에 이름 기반 파라미터를 사용할 수 있다.

</br >

## 값 조회

em.createNativeQuery(SQL)를 사용한다.

~~~
///SQL 정의
String sql = 
	"SELECT ID, AGE, NAME, TEAM_ID " +
	"FROM MEMBER WHERE AGE > ?";
	
Query nativeQuery = em.createNativeQuery(sql)
	.setParameter(1, 10);
	
List<Object[]> resultList = nativeQuery.getResultList();
~~~

두 번째 파라미터를 사용하지 않으면 이렇게 여러 값으로 조회할 수 있다. JPA는 조회한 값들을 Object[]에 담아서 반환한다.
여기서는 스칼라값을 조회했을 뿐이므로 영속성 컨텍스트가 관리하지 않는다.

</br >

## 결과 매핑 사용

엔티티와 스칼라 값을 함께 조회하는 것처럼 매핑이 복잡해지면 @SqlResultSetMapping을 정의해서 결과 매핑을 사용할 수 있다.

~~~
//SQL 정의
String sql = 
	"SELECT M.ID, AGE, NAME, TEAM_ID, I.ORDER_COUNT " +
	"FROM MEMBER M " +
	"LEFT JOIN " +
	"	(SELECT IM.ID, COUNT(*) AS ORDER_COUNT " +
	"	FROM ORDERS O, MEMBER IM " +
	"	WHERE O.MEMBER_ID = IM.ID) I " +
	"ON IM.ID = I.ID";
	
Qeury nativeQuery = em.createNativeQuery(sql, "memberWithOrderCount");
	
List<Object[]> resultList = nativeQuery.getResultList(); 
~~~

이는 회원 엔티티와 회원이 주문한 상품 수를 조회하는 네이티브 쿼리다.
두 번째 파라미터에 결과 매핑 정보의 이름을 사용했다.

~~~
@Entity
@SqlResultSetMapping(name = "memberWithOrderCount",
	entities = (@EntityResult(entityClass = Member.class)),
	columns = (@ColumnResult(name = "ORDER_COUNT"))
)
public class Member {...}
~~~

entities를 통해 엔티티를 매핑하고, cloumns를 통해 컬럼을 매핑할 수 있다.

## @FieldResult를 사용한 컬렴명과 필드명 직접 매핑

~~~
@SqlResutlSetMapping(name = "OrderResults",
	entities={
		@EntityResult(entityClass=com.acme.Order.class, fields={
			@FieldResult(name = "id", column = "order_id"),
			@FieldResult(name = "queantity", column = "order_quantity"),
			@FieldResult(name = "item", column = "order_item")})},
		columns={
			@ColumnResult(name = "item_name")}
	)
~~~

위와같이 컬럼명과 필드명을 직접 매핑할 수 있다.
대신 @FieldResult를 한번이라도 사용하면 전체 필드를 @FiedlResult로 매핑해야 한다.

</br >

## Named 네이티브 SQL

JPQL처럼 SQl도 Named 네이티브 SQL을 사용해서 정적 SQL을 작성할 수 있다.

~~~
@Entity
@NamedNativeQuery(
	name = "Member.memeberSQL"
	qeury = "SELECT ID, AGE, NAME, TEAM_ID " +
		"FROM MEMBER WHERE AGE > ?",
	resultClass = Member.class
)
public class Member {...}
~~~

@NamedNativeQuery를 이용해 Named 네이티브 SQL을 등록했다.
Named 네이티브 SQL을 JPQL Named 쿼리와 같은 createNamedQuery 메서드를 사용한다. 따라서 TypeQuery를 사용할 수 있다.

~~~
TypedQuery<Member> nativeQuery = em.createNamedQuery("Member.memberSQL", Member.class)
	.setParameter(1, 20);
~~~

</br >

## 정리

- 네이티브 SQL도 JPQL을 사용할 때와 마찬가지로 Query, TypeQuery(Named 네이티브 쿼리일 때)를 사용한다. 따라서 JPQL API를 그대로 사용할 수 있다. 
- 네이티브 SQL은 JPQL이 자동 생성하는 SQL을 수동으로 직접 정의하는 것이다.
- 네이티브 SQL은 관리하기 쉽지 않고 자주 사용하면 특정 DB에 종속적인 쿼리가 증가해서 이식성이 떨어진다.
  그러나 네이티브 SQL을 사용하지 않을 수 없다.
- **결론은 될 수 있으면 표준 JPQL을 사용하고 기능이 부족하면 하이버네이트 같은 JPA 구현체가 제공하는 기능을 사용하고**
  **그래도 안되면 네이티브 SQL을 사용하자!**

