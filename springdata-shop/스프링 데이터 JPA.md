# 스프링 데이터 JPA

## 소개

스프링 프레임워크에서 JPA를 편리하게 사용할 수 있도록 지원하는 프로젝트다.

CRUD 처리를 위한 공통 인터페이스를 제공하며, 레포지토리를 개발할 때 인터페이스만 작성하면 실행 시점에 스프링 데이터 JPA가 구현 객체를 동적으로 생성해서 주입해준다. 따라서 데이터 접근 계층을 개발할 때 구현 클래스 없이 인터페이스만 작성해도 개발을 완료할 수 있다.

</br >

## 설정

### 필요 라이브러리

~~~
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
            <version>1.8.0</version>
        </dependency>
~~~

### 환경설정

~~~
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:jpa="http://www.springframework.org/schema/data/jpa"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

    <jpa:repositories base-package="jpabook.jpashop.repository" />
    
</beans>
~~~

스프링 데이터 JPA는 실행 시점에 basePackage에 있는 레포지토리 인터페이스들을 찾아서 해당 인터페이스를 구현한 클래스를 동적으로 생성한 다음 스프링 빈으로 등록한다.

따라서 개발자 직접 구현 클래스를 만들지 않아도 된다.

</ br>

## JpaRepository

스프링 데이터 JPA가 제공하는 인터페이스다. 이를 상속받으면 JpaRepository 인터페이스가 제공하는 다양한 기능을 사용 할 수 있다.

### JpaRepository 주요 메서드

T: 엔티티, ID: 엔티티의 식별자 타입, S: 엔티티와 그 자식 타입

- save(S): 새로운 엔티티는 저장하고 이미 있는 엔티티는 수정한다.(저장 및 수정)
- delete(T): 엔티티 하나를 삭제한다. 내부에서 em.remove()를 호출
- findOnd(ID): 엔티티 하나를 조회한다. 내부에서 em.find()를 호출
- getOne(ID): 엔티티를 프록시로 조회한다. 내부에서 em.getReference()를 호출
- findAll(...): 모든 엔티티를 조회한다.

