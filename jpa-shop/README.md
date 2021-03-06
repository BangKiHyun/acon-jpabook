# 쇼핑몰 시스템

## 기능 정리

- 회원 기능
  - 회원 등록
  - 회원 조회
- 상품 기능
  - 상품 등록
  - 상품 수중
  - 상품 조회
- 주문 기능
  - 상품 주문
  - 주문 내역 조회
  - 주문 취소
- 기타 요구사항
  - 상품의 종류는 도서, 음반, 영화가 있따.
  - 상품을 카테고리로 구분할 수 있다.
  - 상품 주문 시 배송 정보를 입력할 수 있다.

</br >

## 도메인 모델 설계

- 회원, 주문 상품 관계
  - 회원은 여러 상품을 주문할 수 있다(회원:상품 일대다)
  - 한 번 주문할 때 여러 상품을 선택할 수 있다. 다대다 관계를 일대다, 다대일로 바꾸면
    주문:주문상품 일대다, 주문상품:상품 다대일
- 상품 분류
  - 상품은 도서, 음반, 영화로 구분된다.
  - 상품이라는 공통 속성을 사용하여 상속 구조로 만들자

## 도메인 모델

- 회원
  - 이름, 주문상품, Address(임베디드 타입)
- 주문
  - 주문상품, 회원, 배송 정보, 주문 날짜, 주문 상태
  - 주문 상태는 열거형을 사용(ORDER, CANCEL)
- 주문상품
  - 주문한 상품 정보, 주문 금액, 주문 수량
- 상품
  - 이름, 가격, 재고수량
  - 상품을 주문하면 재고수량이 줄어든다.
  - 상품의 종류로는 도서, 음반, 영화가 있다(속성이 조금씩 다름)
- 배송
  - 주문 시 하나의 배송 정보 생성
  - 주문과 배송(일대일)
  - 주문, 주소, 상태
- 카테고리
  - 상품과 다대다 관계
- 주소
  - 값 타입(임베디드 타입)
  - 회원과 배송에서 사용

</br >

## 연관관계 정리

- 회원과 주문
  - 일대다 양방향 관계
  - 연관관계의 주인: 주문
  - Order.member를 OREDERS.MEMBER_ID 외래 키와 매핑
- 주문상품과 주문
  - 다대일 양방향 관계
  - 연관관계 주인: 주문상품
  - OrderItem.order를 ORDER_ITEM.ORDER_ID 외래 키와 매핑
- 주문상품과 상품
  - 다대일 단방향 관계
  - OrderItem.item를 ORDER_ITEM.ITEM_ID 외래 키와 매핑
- 주문과 배송
  - 일대일 양방향 관계
  - Order.delivery를 ORDERS.DELIVERY_ID 외래 키와 매핑
- 카테고리와 상품
  - @ManyToMany를 사용해서 매핑