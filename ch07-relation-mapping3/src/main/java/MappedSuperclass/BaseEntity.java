package MappedSuperclass;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@MappedSuperclass
@NoArgsConstructor
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}

@Entity
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME"))
})
@NoArgsConstructor
@Getter
class Member extends BaseEntity {

    //ID 상속
    //NAME 상속
    private String email;

}

@Entity
@NoArgsConstructor
@Getter
class Seller extends BaseEntity {

    //ID 상속
    //NAME 상속
    private String shopName;

}