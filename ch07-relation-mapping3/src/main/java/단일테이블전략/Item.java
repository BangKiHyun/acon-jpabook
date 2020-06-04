package 단일테이블전략;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@DiscriminatorColumn(name = "DTYPE")
@Getter
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Book")
@PrimaryKeyJoinColumn(name = "BOOK_ID") //ID 재정의
class Book extends Item {
    private String author;
    private String isbn;

    @Builder
    public Book(String name, int price, String author, String isbn) {
        super(name, price);
        this.author = author;
        this.isbn = isbn;
    }
}

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Album")
class Album extends Item {
    private String artist;

    @Builder
    public Album(String name, int price, String artist) {
        super(name, price);
        this.artist = artist;
    }
}

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Movie")
class Movie extends Item {
    private String actor;
    private String director;

    @Builder
    public Movie(String name, int price, String actor, String director) {
        super(name, price);
        this.actor = actor;
        this.director = director;
    }
}
