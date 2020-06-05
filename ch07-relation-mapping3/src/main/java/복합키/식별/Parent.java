package 복합키.식별;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Parent {

    @Id
    @Column(name = "PARENT_ID")
    private String id;

    private String name;
}
