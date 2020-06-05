package 복합키.비식별;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@IdClass(ParentId.class)
@NoArgsConstructor
@Getter
public class Parent {

    @Id
    @Column(name = "PARENT_ID1")
    private String id1;

    @Id
    @Column(name = "PARENT_ID2")
    private String id2;

//    @EmbeddedId
//    private ParentId parentId;

    private String name;
}
