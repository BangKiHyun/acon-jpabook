# QueryDSL

## QueryDSL 설정

### 필요 라이브러리

~~~
        <!-- QueryDSL 설정 -->
        <dependency>
            <groupId>com.mysema.querydsl</groupId>
            <artifactId>querydsl-jpa</artifactId>
            <version>3.6.3</version>
        </dependency>

        <dependency>
            <groupId>com.mysema.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
            <version>3.6.3</version>
        </dependency>
~~~

- querydsl-jpa: QueryDSL JPA 라이브러리
- querydsl-apt: 쿼리 타입(Q)을 생성할 때 필요한 라이브러리

### 환경 설정

~~~
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>

~~~

콘솔에 mvn complie을 입력하면 outputDirectory에 지정한 target/generated-sorces위치에 Member.java와 같은 Q로 시작하는 쿼리 타입들이 생성된다.

</br >

### 검색 조건 쿼리

~~~
JPAQuery query = new JPAQuery(em);
QItem item = QItem.item;
List<Item> list =
         query.from(item)
         .where(item.name.eq("좋은상품").and(item.price.gt(20000)))
         .list(item); //조회학 프로젝션 지정
~~~

위의 쿼리를 실행하면 다음과 같은 JPQL이 생성된다.

~~~
select item
from item
where itemname = ?1 and item.price > ?2
~~~

쿼리 타입의 필드는 필요한 대부분의 메서드를 명시적으로 제공한다.

- item.price.between(10000, 20000); //가격이 10000~20000원 상품
- item.name.contains("상품1"); //상품1이라는 이름을 포함한 상품
  SQL: like '%상품%'
- item.name.startWith("고급"); //이름이 고급으로 시작하는 상품
  SQL: like '고급%'

</br >

### 결과 조회

쿼리 작성이 끝나고 결과 조회 메서드를 호출하면 실제 DB를 조회한다.
보통 uniqueResult()나 list()를 사용하며 파라미터로 프로젝션 대상을 넘겨준다.

- uniqueResult(): 조회 결과가 한 건일 때 사용.
  조회 결과가 없으면 null반환, 하나 이상이면 NonUniqueResultException발생
- singleResult(): uniqueResult()와 같지만 결과가 하나 이상이면 처음 데이터를 반환
- list(): 결과가 하나 이상일 때 사용.
  결과가 없으면 빈 컬렉션 반환

</br >

### 페이징과 정렬

정렬은 orderBY를 사용하며 쿼리 타입(Q)이 제공하는 asc(), desc()를 사용한다.
페이징은 offset과 limit을 조합해서 사용하면 된다.

~~~
QItem item = QItem.item;

query.from(item)
	.where(item.price.gt(20000))
	.orderBy(item.price.desc(), item.stockQuantity.asc())
	.offset(10).limit(20)
	.list(item);
~~~

페이징은 restrict() 메서드에 QueryModifiers를 파라미터로 사용해도 된다.

~~~
QueryModifiers queryModifiers = new QueryModifiers(20L, 10L); //limit, offset
List<Item> list =
	query.from(item)
	.restrict(queryModifiers)
	.list(item);
~~~

실제 페이징 처리를 하려면 검색된 전체 데이터 수를 알아야 한다. 이때 list()대신 listResults()를 사용한다.

~~~
SearchResults<Item> result = 
	query.from(item)
		.where(item.price.gt(20000))
		.offset(10).limit(20)
		.list(item);
		
long total = result.getTotal(); //검색된 전체 데이터 수
long limit = result.getLimit();
long offset = result.getOffset();
List<Item> results = result.getResults(); //조회된 데이터
~~~

listResults()를 사용하면 전체 데이터 조회를 위한 couont쿼리를 한번 더 실행하여 SearchResults를 반환한다.
이를 통해 전체 데이터 수를 조회할 수 있다.

</br >

### 서브 쿼리

서브 쿼리는 JPASubQuery를 생성해서 사용한다.
서브 쿼리 결과가 하나면 unique(), 여러 건이면 list()를 사용할 수 있다,

~~~
QItem item = QItem.item;
QItem itemSub = new QItem("itemSub");

query.from(item)
	where(Item.price.eq(
		new JPASubQuery().from(itemSub).unique(itemSub.price.max())
	))
	.list(item);
~~~

</br >

### 여러 컬럼 반환과 튜플

select절에 조회 대상(프로젝션)으로 여러 필드를 선택하면 QueryDSL은 기본으로 Tuple이라는 Map과 비슷한 내부 타입을 사용한다. 조회 결과는 tuple.get() 메서드에 조회한 쿼리 타입을 지정하면 된다.

~~~
QItem item = QItem.item;

List<Tuple> result = query.from(item).list(item.name, item.price);
//List<Tuple> result = query.from(item).list(new QTuple(item.name, item.price));
//같다.

for(Tuple tuple : result) {
	System.out.println("name = " + tuple.get(item.name)):
	System.out.println("price = " + tuple.get(item.price)):
}
~~~

</br >

### 빈 생성

쿼리 결과를 엔티티가 아닌 특정 객체로 받고 싶으면 빈 생성 기능을 사용한다.
원하는 방법을 지정하기 위해 Projections를 사용하면 된다.

~~~
public calss ItemDTO {
	private String username;
	private int price;
	
	public Item DTO(String username, int price){
		this.username = username;
		this.price = price;
	}
	
	//...Setter, Getter
}
~~~

#### 프로퍼티(Setter) 접근방법

~~~
QItem item = QItem.item;
List<ItemDTO> result = qeury.form(item).list(
	Projections.bean(ItemDTO.class, item.name.as("username", item.price));
~~~

bean()메서드는 수정자(Setter)를 사용해서 값을 채운다.
예제를 보면 쿼리 결과는 name이지만 ItemDTO의 username프로퍼티와 매치시키기 위해 as를 사용해 별칭을 준다.

#### 필드 직접 접근

~~~
QItem item = QItem.item;
List<ItemDTO> result = qeury.form(item).list(
	Projections.fields(ItemDTO.class, item.name.as("username", item.price));
~~~

fields()메서드는 필드에 직접 접근해서 값을 채워준다. 필드의 접근제한자를 private으로 설정해도 동작한다.

#### 생성자 사용

~~~
QItem item = QItem.item;
List<ItemDTO> result = qeury.form(item).list(
	Projections.constructor(ItemDTO.class, item.name, item.price);
~~~

constructor()메서드는 생성자를 사용한다.
지정한 프로젝션과 파라미터 순서가 같은 생성자가 필요하다.

