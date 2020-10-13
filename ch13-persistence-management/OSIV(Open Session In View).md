# OSIV(Open Session In View)

## OSIV란?

영속성 컨텍스트를 뷰까지 열어둔다는 뜻으로, 뷰에서도 지연 로딩을 사용할 수 있게 도와준다.

## 스프링 OSIV: 비즈니스 계층 트랜잭션

스프링 프레임워크의 spring-orm.jar는 다양한 OSIV클래스를 제공한다.

OSIV를 서블릿 필터에서 적용할지 스프링 인터셉터에서 적용할지에 따라 원하는 클래스를 선택해서 사용할 수 있다.

- 하이버네이트 OSIV 서블릿 필터: org.springframework.orm.hibernated4.support.OpenSessionInViewFilter
- 하이버네이트 OSIV 스프링 인터셉터: org.springframework.orm.hibernated4.support.OpenSessionInViewInterceptor
- JPA OEIV 서블릿 필터: org.springframework.orm.jpa.support.OpenSessionInViewFilter
- JPA OEIV 인터셉터 필터: org.springframework.orm.jpa.support.OpenSessionInViewInterceptor

</br >

## 스프링 OSIV 분석

동작 원리는 다음과 같다.

1. 클라이언트의 요청이 들어오면 서블릿 필터나, 스프링 인터셉터에서 영속성 컨텍스트를 생성한다.

   이때 트랜잭션은 시작하지 않는다.

2. 서비스 계층에서 @Transactional로 트랜잭션을 시작할 때 1번에서 미리 생성해둔 영속성 컨텍스트를 찾아와서 트랜잭션을 시작한다.

3. 서비스 계층이 끝나면 트랜잭션을 커밋하고 영속성 컨텍스트를 플러시한다.

   이때 트랜잭션은 끝내지만 영속성 컨텍스트는 종료하지 않는다.

4. 컨트롤러와 뷰까지 영속성 컨텍스트가 유지되므로 조회한 엔티티는 영속 상태를 유지한다.

5. 서블릿 필터나, 스프링 인터셉터로 요청이 돌아오면 영속성 컨텍스트를 종료한다.

   이때 플러시를 호출하지 않고 바로 종료한다.

과거 OSIV(요청당 트랜잭션) 방식은 프리젠테이션 계층에서 데이터를 변경할 수 있다는 문제가 있다. 데이터를 변경할 수 없게 하려면 래퍼 클래스나 DTO를 만들어야 했는데 그럼 코드의 양이 방대해진다. 위와같은 문제를 스프링 OSIV에서 어느 정도 해결했다.

</br >

### 트랜잭션 없이 읽기(Nontransactional reads)

영속성 컨텍스트를 통한 모든 변경은 트랜잭션 안에서 이뤄져야한다. 만약 트랜잭션 없이 엔티티를 변경하고 영속성 컨텍스트를 플러시하면 TransactionRequiredException예외가 발생한다.

엔티티를 변경하지 않고 단순히 조회만 할 때는 트랜잭션이 없어도된다. 이것을 **트랜잭션 없이 읽기**라 한다.프록시를 초기화하는 지연 로딩도 조회 기능이므로 트랜잭션 없이 읽기가 가능하다. 정리하면 다음과 같다.

- 영속성 컨텍스트는 트랜잭션 범위 안에서 엔티티를 조회하고 수정할 수 있다.
- 영속성 컨텍스트는 트랜잭션 범위 밖에서 엔티티를 조회만 할 수 있다.(트랜잭션 읽기)

### 스프링 OSIV 특징

위에서 보았던 특징을 근거로 보았을때 스프링 OSIV는 다음과 같은 특징을 가진다.

- 영속성 컨텍스트를 프리젠테이션 계층까지 유지한다.
- 프리젠테이션 계층에는 트랜잭션이 없으므로 엔티티를 수정할 수 없다.
- 프리젠테이션 계층에는 트랜잭션이 없지만 트랜잭션 없이 읽기를 사용해서 지연 로딩을 할 수 있다.

### 예제

~~~
class MemberController {
	
	public String viewMember (Long id) {
	
		Member member = memberService.getMember(id);
		memeber.setName("XXX"); //보안상의 이유로 고객 이름을 XXX로 변경
		model.addAttribute("member", member);
	}
}
~~~

위 코드는 보안상의 이유로 뷰에서 보여줄 이름을 XXX로 변경했다. 만약 영속성 컨텍스트가 살아있고, 플러시를 했다면 변경 감지가 동작해서 DB에 해당 회원의 이름을 XXX로 변경하는 문제가 생길것이다.

하지만 스프링 OSIV를 적용하면 다음과 같은 이유로 플러시가 동작하지 않는다.

- 트랜잭션을 사용하는 서비스 계층이 끝날 때 트랜잭션이 커밋되면서 이미 플러시를했다. 그리고 스프링이 제공하는 OSIV 필터나 OSIV 스프링 인터셉터는 요청이 끝나면 플러시를 호출하지 않고 em,close()로 영속성 컨텍스트만 종료해 버리므로 플러시가 일어나지 않는다.
- 프리젠테이션 계층에서 em.flush()를 호출해서 강제로 플러시해도 트랜잭션 범위 밖이므로 데이터를 수정할 수 없다는 예외를 발생한다.

</br >

## 스프링 OSIV 주의사항

스프링 OSIV는 프리젠테이션 계층에서 엔티티를 수정해도 수정 내용을 DB에 반영하지 않는다.

하지만 **프리젠테이션 계층에서 엔티티를 수정한 직후 트랜잭션을 시작하는 서비스 계층을 호출하면 문제가 발생**한다.

~~~
class MemberController {
	
	public String viewMember (Long id) {
	
		Member member = memberService.getMember(id);
		memeber.setName("XXX"); //보안상의 이유로 고객 이름을 XXX로 변경
		
		memberService.biz(); //비즈니스 로직
		return "view";
	}
}
~~~

위 코드를 분서해 보면 다음과 같다.

1. 컨트롤러에서 회원 엔티티를 조회하고 이름을 member.setName("XXX")로 수정

2. biz() 메서드를 실행해서 트랜잭션이 있는 비즈니스 로직 실행

3. 트랜잭션 AOP가 동작하면서 영속성 컨텍스트에 트랜잭션을 시작. 그리고 biz() 메서드를 실행

4. biz() 메서드가 끝나면 트랜잭션 AOP는 트랜잭션을 커밋하고 영속성 컨텍스트를 플러쉬

   이때 변경 감지가 동작하면서 회원 엔티티의 수정 사항을 DB에 반영

위와 같이 엔티티를 수정한 후 비즈니스 로직을 실행시켰을때 문제가 발생한다.

해결 방법은 비즈니스 로직을 모두 호출하고 나서 엔티티를 변경하면 되는 단순한 방법이 있다.

</br >

## OSIV 정리

- ### 스프링 OSIV의 특징

  - OSIV는 클라이언트의 요청이 들어올 때 영속성 컨텍스트를 생성해서 요청이 끝날 때까지 같은 영속성 컨텍스트를 유지한다.

    따라서 한 번 조회한 엔티티는 요청이 끝날 때까지 영속 상태를 유지한다.

  - 엔티티 수정은 트랜잭션이 있는 계층에서만 동작한다. 트랜잭션이 없는 프리젠테이션 계층을 지연 로딩을 포함해서 조회만 할 수 있다.

- ### 스프링 OSIV의 단점

  - OSIV를 적용하면 같은 영속성 컨텍스트를 여러 트랜잭션이 공유할 수 있다는 점을 주의해야 한다.
  - 프리젠테이션 계층에서 엔티티를 수정하고나서 비즈니스 로직을 수행하면 엔티티가 수정된다.

- ###  만능은 아니다

  - 복잡한 통계 화면은 엔티티로 조회하기 보다는 처음부터 통계 데이터를 구상하기 위한 JPQL을 작성해서 DTO로 조회하는 것이 효과적이다.

  - 수많은 테이블을 조인해서 보여주어야 하는 복잡한 관리자 화면도 객체 그래프로 표현하기 어려운 경우가 많다.

    