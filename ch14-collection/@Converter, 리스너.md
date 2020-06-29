# @Converter

컨버터를 사용하면 엔티티의 데이터를 변환해서 데이터베이스에 저장할 수 있다.

## 예제

자바의 boolean 타입을 사용할 때, JPA를 사용하면 자바의 boolean타입은 데이터베이스에 저장될 때 0 또는 1인 숫자로 저장된다. 그런데 데이터베이스에 숫자 대신 문자 Y 또는 N 으로 저장하고 싶다면 컨버터를 사용하면된다.

~~~
@Entity
public class Memeber {

	@Id
	private String id;
	private String username;
	
	@Convert(converter=BooleanToYNConerter.class)
	private boolean vip;
}
~~~

@Convert를 적용해서 데이터베이스에 저장되기 직전에 BooleanYNConverter 컨버터가 동작하도록 했다.

~~~
@Converter
public class BooleanYNConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn (Boolean attribute) {
		return (attribute != null && attribute) ? "Y" : "N";
	}
	
	@Override
	public Boolean convertToEntityAttribute(String data) {
		return "Y".equals(dbData);
	}
}
~~~

</br >

## 컨버터 클래스

컨버터 클래스는 @Converter 어노테이션을 사용하고 AttributeConverter 인터페이스를 구현해야 한다.

제네릭에는 현재 타입과 변환할 타입을 지정해야 한다.(여기서는 Boolean 타입을 String 타입으로 변환)

AttributeConverter 인터페이스에는 구현해야할 두 메서드가 있다.

- ### convertToDatabaseColumn()

  - 엔티티의 데이터를 데이터베이스 컬럼에 저장할 데이터로 변환
  - 여기서는 true면 Y, false면 N을 반환

- ### convertToEntityAttribute()

  - 데이터베이스에서 조회한 컬럼 데이터를 엔티티의 데이터로 변환
  - 여기서는 문자 Y면 true, 아니면 false를 반환

</br >

## 글로벌 설정

모든 Boolean 타입에 컨버터를 적용하려면 @Converter(autoApply = true) 옵션을 적용하면 된다.

~~~
@Converter(autoApply = true)
public class BooleanYNConverter implements AttributeConverter<Boolean, String> {
	...
}
~~~

위와 같이 글로벌 설정을 하면 @Converter를 지정하지 않아도 모든 Boolean타입에 대해 자동으로 컨버터가 적용된다.

</br >

## 속성 정리

| 속성              | 기능                                                | 기본값 |
| ----------------- | --------------------------------------------------- | ------ |
| converter         | 사용할 컨버터를 지정                                |        |
| attributeName     | 컨버터를 적용할 필드를 지정                         |        |
| disableConversion | 글로벌 컨버터나 상속 받은 컨버터를 사용하지 않는다. | false  |

</br >

# 리스너

JPA 리스너 기능을 사용하면 엔티티의 생명주기에 따른 이벤트를 처리할 수 있다.

## 이벤트 종류

![image-20200629181610074](/Users/bang/Library/Application Support/typora-user-images/image-20200629181610074.png)

- PostLoad: 엔티티가 영속성 컨텍스트에 조회된 직후 또는 refresh를 호출한 후
- PrePersist: persist() 메서드를 호출해서 엔티티를 영속성 컨텍스트에 관리하기 직전에 호출
- PreUpdate: flush나 commit을 호출해서 엔티티를 데이터베이스에 수정하기 직전에 호출
- PreRemove: remove() 메서드를 호출해서 영속성 컨텍스트에서 엔티티를 삭제하기 직전에 호출
- PostPersist: flush나 commit을 호출해서 엔티티를 데이터베이스에 저장한 직후에 호출
  - 식별자가 항상 존재한다.
  - 식별자 생성 전략이 IDENTITY면 식별자를 생성하기 위해 persist()를 호출하면서 데이터베이스에 해당 엔티티를 저장하므로 persist()를 호출한 직후에 바로 PostPersist가 호출된다.
- PostUpdate: flush나 commit을 호출해서 엔티티를 데이터베이스에 수정한 직후에 호출
- PostRemove: flush나 commit을 호출해서 엔티티를 데이터베이스에 삭제한 직후에 호출

</br >

## 이벤트 적용 방법

- 엔티티에 직접 적용
- 별도의 리스너 등록
- 기본 리스너 사용

### 엔티티에 직접 적용

~~~
@Entity
public class Duck {
	
	@Id @GeneratedValue
	public Lond id;
	
	private String name;
	
	@PrePersist
	public void prePersist() {
		...
	}
	
	@PostPersist
	public void postPersist() {
		...
	}
}
~~~

엔티티에 이벤트가 발생할 때마다 어노테이션으로 지정한 메서드가 실행된다.

</br >

### 별도의 리스너 등록

~~~
@Entity
@EntityListeners(DuckListener.class)
public class Duck {
	...
}

public class DickListener{
	@PrePersist
	private void prePersist(Object obj) {
		...
	}
	
	@PostPersist
	private void postPersist(Object obj) {
		...
	}
}
~~~

리스너는 대상 엔티티를 파라미터로 받을 수 있다. 반환 타입은 void로 설정해야 한다.

</br >

### 기본 리스너 사용

모든 엔티티의 이벤트를 처리하려면 META-INF/orm.xml에 기본 리스너로 등록하면 된다.

~~~
<persistence-unit-metadata>
	<persistence-unit-defaults>
		<entity-listeners>
			<entity-listener class="jpabook.jpashop.domain.test.listener.DefaultListener" />
		</entity-listeners>
	</persistence-unit-defaults>
</persistence-unit-metadata>
~~~

여러 리스너를 등록했을 때 이벤트 호출 순서

1. 기본 리스너
2. 부모 클래스 리스너
3. 리스너
4. 엔티티

### 세밀한 설정

- @ExcludeDefaltListeners: 기본 리스너 무시
- @ExcluddeSuperclassListeners: 상위 클래스 이벤트 리스너 무시

</br ?

## 정리

이벤트를 잘 활용하면 대부분의 엔티티에 공통으로 적용하는 등록 일자, 수정 일자 처리와 해당 엔티티를 누가 등록하고 수정했는지에 대한 기록을 리스너 하나로 처리할 수 있다.