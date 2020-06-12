## JPQL(Java Persistence Query Language)

- JPQL은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 엔티티 객체를 대상으로 쿼리한다.
- JPQL은 SQL을 추상화해서 특정 데이버에이스 SQL에 의존하지 않는다.
- JPQL은 결국 SQL로 변환된다.

</br >

### 기본 문법과 쿼리 API

JQPL도 SQL과 비슷하게 SELECT, UPDATE, DELETE 문을 사용할 수 있다.
엔티티를 저장할 때는 EntityManaber.persist() 메서드를 사용하면 되므로 INSERT문은 없다.

### SELECT 문

~~~
SELECT m FROM Member AS m where m.username = 'Hello'
~~~

- 대소문자 구분
  - 엔티티와 속성은 대소문자를 구분한다.(Member, username)
  - JQPL 키워드는 대소문자를 구분하지 않는다.(SELECT, FROM, AS)
- 엔티티 이름
  - JPQL에서 사용한 Member는 클래스명이 아닌 엔티티 명이다.
  - 엔티티 명은 @Entity(name="OOO")로 지정할 수 있다.
  - 엔티티 명을 저장하지 않으면 클래스명을 기본값으로 사용한다.
    **기본값인 클래스 명을 엔티티 명으로 사용하는 것을 추천**한다.
- 별칭(식별 변수)은 필수
  - Member As m 을 보면 Member에 m이라는 별칭을 주었다. JPQL은 별칭을 필수로 사용해야 한다.
  - AS는 생략할 수 있다.(Member m)



### TypeQuery, Query

작성한 JQPL을 실행하기 위한 쿼리 객체를 의미한다.
**반환 타입을 명확하게 지정할 수 있으면 TypeQuery**를,
객체를 사용하고, **반환 타입을 명확하게 지정할 수 없으면 Query 객체**를 사용한다.

~~~
//TypeQuery 사용 예
TypedQuery<Member> query =
	em.createQuery("SELCET m FROM Member m", Member.class)
	
List<Member> resultList = query.getResultList();
for (Member member : resultList){
	System.out.pringln("member = " + member);
}
~~~

조회 대상이 Member 엔티티로 조회 대상 타입이 명확할 때 위와 같이 TypeQuery를 사용할 수 있다.

~~~
Query query = 
	em.createQuery("SLECT m.username, m.age from Member m");
	
List resultList = query.getResultList();

for(Object o : resultList){
	Object[] result = (Object[]) o; //결과가 둘 이상이면 Object[] 반환
	System.out.println("username = " + result[0]);
	System.out.println("age = " + result[1]);
}
~~~

조회 대상이 String 타입의 이름과 Integer 타입의 나이이므로 조회 대상 타입이 명확하지 않을 때 위와 같이 Query 객체를 사용하면 된다.
Query객체는 SELECT 절의 조회 대상이 둘 이상이면 Object[]를 반환하고 하나면 Object를 반환한다.

두 코드를 비교해 봤을 때 타입을 변환할 필요가 없는 TypeQuery가 더 편리하다.

</br >

### 결과 조회

다음 메서드들을 호출하면 실제 쿼리를 실행해서 데이터베이스를 조회한다.

- query.getResultList(): 결과를 예제로 반환한다. 만약 결과가 없으면 빈 컬렉션을 반환한다.
- query.getSingelResult(): 결과가 정확히 하나일 때 사용한다.
  - 결과가 없으면 NoResultException예외가 발생한다.
  - 결과가 1개보다 많으면 NonUniqueResultException 예외가 발생한다,

</br >

### 파라미터 바인딩

JPQL은 이름 기준 파라미터 바인딩을 지원한다.

- 이름 기준 파라미터

  파라미터를 이름으로 구분하는 방법이다. 이름 기준 파라미터는 앞에 :를 사용한다.

  ~~~
  String usernameParam = "user1";
  
  TypeQuery<Member> query = 
  	em.createQuery("SELECT m FROM Member m where m.username = :username", Member.class);
  	
  	query.setParameter("username", usernameParam);
  	List<Member> resultList = query.getResultList();
  ~~~

  query.setParameter()를 통해 username이라는 이름으로 파라미터를 바인딩한다.
  참고로 JPQL API는 대부분 *메서드 체인 방식으로 설계되어 있어서 다음과 같이 연속해서 작성할 수 있다.

  ~~~
  List<Member> members =
  	em.createQuery("SELECT m FROM Member m where m.username = :username", Member.class);
  		.setParameter("username", usernameParam)
  		.getResultList();
  ~~~

  > 메서드 체인 방식이란 메서드가 객체를 반환하게 되면, 메서드의 반환 값인 객체를 통해 또 다른 함수를 호출하는 방식을 말한다.

- 위치 기준 파라미터

  위치 기준 파라미터를 사용하려면 ? 다음에 위치 값을 주면 된다. 위치 값은 1부터 시작한다.

  ~~~
  List<Member> members =
  	em.createQuery("SELECT m FROM Member m where m.username
  	= ?1", Member.class)
  	.setParameter(1, usernameParam)
  	.getResultList();
  ~~~

위치 기준 파라미터 방식보다 **이름 기준 파라미터 바인딩 방식을 사용하는 것이 더 명확하다.**

### 파라미터 바인딩 장점

- 파라미터의 값이 달라도 같은 쿼리로 인식해서 JPA는 JPQL을 SQL로 파싱한 결과를 재사용할 수 있다.
- 데이터베이스 내부에서 실행한 SQL을 파싱해서 사용하는데 같은 쿼리는 파싱한 결과를 재사용할 수 있다.
- 결과적으로 해당 쿼리의 파싱 결과를 재사용할 수 있어서 전체 성능이 향상된다.

파라미터 바인딩 방식은 선택이 아닌 **필수**다!!!

