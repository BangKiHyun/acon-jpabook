## 복합키와 식별 관계 매핑

### 식별 관계

식별 관계는 부모 테이블의 기본 키를 자식 테이블이 받아서 기본 키 + 외래 키로 사용하는 관계다.

### 비식별 관계

비식별 관계는 부모 테이블의 기본 키를 받아서 자식 테이블의 외래 키로만 사용하는 관계다.

- 필수적 비식별 관계: 외래 키에 NULL을 허용하지 않는다.
  즉, 연관관계를 필수적으로 맺어야 한다.
- 선택적 비식별 관계: 외래 키에 NULL을 허용한다.
  즉, 연관관계를 맺을지 말지 선택할 수 있다.



### 복합 키: 비식별 관계 매핑

기본 키를 구성하는 컬럼이 둘 이상이면 오류가 발생한다.

~~~
@Entity
public class Hello {
	
	@Id
	private String id1;
	
	@Id
	private String id2; //실행 시점에 매핑 예외 발생
}
~~~

JPA에서 식별자를 둘 이상 사용하려면 별도의 식별자 클래스를 만들어야 한다.
JPA는 영속성 컨텍스트에 엔티티를 보관할 때 엔티티의 식별자를 키로 사용한다.

식별자 필드가 2개 이상일 때 별도의 식별자 클래스를 만들고 그 곳에서
equals와 hashCode를 사용해서 동등성 비교를 해야 한다.

### @IdClass

식별자 클래스를 지정한다.
관계형 데이터베이스에 가까운 방법이다.

### 식별자 클래스 만족 조건

- 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.
- Serializable 인터페이스를 구현해야 한다.
- equals, hashCode를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public 이어야 한다.

~~~
Parent parent = new Parent();
parent.setId1("myId1");
parent.setId2("myId2");
parent.setName("parentName");
em.persist(parent);
~~~

위의 저장 코드를 분석해보면
em.persist() 호출시 영속성 컨텍스트에 엔티티를 등록하기 전 내부에서 Parent.id1,Parent.id2 값을 사용해서 식별자 클래스인 ParentId를 생성하고 영속성 컨텍스트의 키로 사용된다.



### @EnbeddedId

@IdClass보다 객체지향에 가까운 방법이다.
식별자 클래스에 기본 키를 직접 매핑한다.
@IdClass와 다르게 @EnbeddedId를 적용해 식별자 클래스에 기본 키를 직접 매핑한다.

Parent 엔티티에서 **식별자 클래스를 직접 사용**하고, @EmbeddedId 어노테이션을 적어주면 된다.

~~~
@EmbeddedId
private ParentId parentId;
~~~

### 식별자 클래스 만족 조건

- @Embeddable 어노테이션을 붙여줘야 한다.
- Serializable 인터페이스를 구현해야 한다.
- equals, hashCode를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public 이어야 한다.

~~~
Parent parent = new Parent();
ParendId parendId = new ParentId("myId1", "myId2");
parent.setId(parentId);
parent.setName("parentName");
em.persist(parent);
~~~

위 저장 코드를 보면 식별자 클래스 parentId를 직접 생성해서 사용한다.

@EmbeddedId가 @IdClass에 비해 더 객체지향적이고 중복도 없다.
하지만 특정 상황에 JPQL이 조금 더 길어질 수 있다.

~~~
em.createQuery("select p.id.id1, p.id.id2 from Parent p"); // @EmbeddedId
em.createQuery("select p.id1, p.id2 from Parent p"); // @IdClass
~~~



### 복합 키: 식별 관계 매핑

### @IdClass

식별 관계는 기본 키와 외래 키를 같이 매핑해야 한다.
따라서 식별 매핑인 @Id와 연과놔계 매핑인 @ManyToOne을 같이 사용해야 한다.

~~~
    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;
~~~



### @EmbeddedId

@EmbeddedId로 식별 관계를 구서할 때 @MapsId를 사용해야 한다.

~~~
@EmbeddedId
private ChildId id;

@MapsId("parentId") //ChildId.parentId 매핑
@Id
@ManyToOne
@JoinColumn(name = "PARENT_ID")
private Parent parent;

------------------------------------------------------------------------

private String parentId; //@MapsId("parentId")로 매핑

@Column(name = "CHILD_ID")
private String id;
~~~



### 결론

식별 관계보다 비식별 관계를 사용하라.

- 식별 관계는 부모 테이블의 기본 키를 자식 테이블로 전파하면서 자식 테이블의 기본 키 컬럼이 점점 늘어난다.
  결국 조인할 때 SQL이 복잡해지고 기본 키 인덱스가 불필요하게 커질 수 있다.
- 식별 관계는 2개 이상의 컬럼을 합해서 복합 기본 키를 만들어야 하는 경우가 많다.
- 식별 관계는 기본 키로 자연 키 컬럼을 조합하는 경우가 많다.
  반면에 비식별 관계는 기본 키로 대체 키 컬럼을 주로 사용한다.
- 대체 키는 JPA에서 @GenerateValue와 같은 대체 키를 생성하기 위한 방법을 제공한다.

식별 관계의 장점을 말하자면 특정 상황에 조인 없이 하위 테이블만으로 검색을 완료할 수 있다.
(하위 테이블이 상위 테이블의 기본 키를 알고 있기 때문에)



### 정리

- 될 수 있으면 비식별 관계를 사용하고 기본 키는 Long 타입의 대리 키를 사용하자.
  - 대리 키는 비즈니스가 변경되어도 영향이 없다.
  - JPA에서 @GenerateValue를 통해 간편하게 대리 키를 생성할 수 있다.
- 선택적 비식별 관계보다 필수적 비식별 관계를 사용하라.
  - 선택적 비식별 관계는 NULL을 허용하므로 외부 조인을 사용해야 한다.
  - 반면에 필수적 비식별 관계는 NOT NULL로 내부 조인만 사용해도 된다.