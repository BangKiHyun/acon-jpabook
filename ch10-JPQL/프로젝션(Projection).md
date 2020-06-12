## 프로젝션(Projection)

SELECT 절에 조회할 대상을 지정하는 것을 프로젝션이라 한다. **[SELECT {프로젝션 대상} FROM]** 으로 대상을 정한다.
프로젝션 대상은 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)이 있다.

</br >

### 엔티티 프로젝션

~~~
SELECT m FROM Member m
SELECT m.team FROM Member m
~~~

위 코드는 둘다 엔티티를 프로젝션 대상으로 사용했다.
즉, 원하는 객체를 바로 조회한 것이다. 컬럼을 하나하나 나열해서 조회하는 SQL과 차이가 있다.
**이렇게 조회한 엔티티는 영속성 컨텍스트에서 관리된다.**

</br >

### 임베디드 타임 프로젝션

임베디드 타입은 엔티티와 거의 비슷하게 사용된다. 다만 **임베디드 타입은 조회의 시작점이 될 수 없다**는 제약이 있다.

다음은 임베디드 타입인 Address를 조회의 시작점으로 사용해 잘못된 쿼리다.

~~~
String query = "SELECT a FROM Address a";
~~~

다음은 Order 엔티티가 시작점으로, 임베디드 타입을 조회한 올바른 코드다.

~~~
String query = "SLELCT o.address FROM Order o";
List<Address> addresses = em.createQuery(query, Address.class)
			.getResultList();
~~~

실행된 SQL

~~~
select
	order.city,
	order.street,
	order.zipcode
from
	Orders order
~~~

**임베디드 타입은 엔티티 타입이 아닌 값 타입이므로, 임베디드 타입은 영속성 컨텍스트에서 관리되지 않는다.**

</br >

### 스칼라 타입 프로젝션

숫자, 문자, 날짜와 같은 기본 데이터 타입들을 스칼라 타입이라 한다.

다음은 전체 회원 이름을 조회하는 쿼리다.

~~~
List<String> usernames = 
	em.createQuery("SELECT username FROM Member m", String.class)
		.getResulList();
~~~

다음과 같은 통계 쿼리도 주로 스칼라 타입으로 조회한다.

~~~
Double orderAmountAvg =
	em.createQuery("SELECT AVG(o.orderAmount) FROM Order o", Double.class)
		.getSingleResult();
~~~

</br >

### 여러 값 조회

꼭 필요한 데이터들만 선택해서 조회해야 할 때 사용한다.
프로젝션에 여러 값을 선택하면 TypeQuer대신 Query를 사용해야 한다.

~~~
List<Object[]> resultList =
	em.createQuery("SELECT m.username, m.age FROM Member m")
		.getResultList();

for(Object[] row : resultList) {
	String username = (String) row[0];
	Integer age = (Integer) row[1];
}
~~~

스칼라 타입뿐만 아니라 엔티티 타입도 여러 값을 함께 조회할 수 있다.

~~~
List<Object[]> resultList =
	em.createQuery("SELECT o.member, o.orderAmount FROM Order o");
		.getResultList();

for(Object[] row : resultList) {
	Member member = (Member) row[0]; //엔티티
	int orderAmount = (Integer) row[1]; //스칼라
}
~~~

</br >

### NEW 명령어

여러 값을 조회할 때 여러 필드를 프로젝션해서 타입을 지정할 수 없으므로 Object[]를 반환받았다. 하지만 실무에서는 DTO와 같은 의미 있는 객체로 변환해서 사용할 것이다.

~~~
List<Object[]> resultList =
	em.createQuery("SELECT m.username, m.age FROM member m")
		.getResultList();
		
List<UserDTO> userDTOs = new ArrayList<UserDTO>();
for(Object[] row : resultList) {
	UserDto userDTO = new UserDTO((Sting)row[0], (Integer)row[1]);
	userDTOs.add(userDTO);
}
return userDTOs;
~~~

위와 같이 객체 변환 작업을 해서 반환해주는 방법이 있지만 **NEW 명령어를 사용**하면 변환 작업을 할 필요가 없어진다.

~~~
TypeQuery<UserDTO> query =
	em.createQuery("SELECT new jpabook.jpql.UserDTO(m.username, m.age)
	FROM Member m", UserDTO.class);
	
List<UserDTO> resultList = query.getResultList();
~~~

위와 같이 NEW 명령어를 사용하여 반환받을 클래스를 지정할 수 있다.

### NEW 명령어 사용시 주의사항

- **패키지 명을 포함한 전체 클래스 명을 입력**해야 한다.
- **순서와 타입이 일치**하는 생성자를 사용해야 한다.

