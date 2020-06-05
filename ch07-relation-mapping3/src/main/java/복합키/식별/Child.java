package 복합키.식별;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@IdClass(ChildId.class)
@NoArgsConstructor
@Getter
@Setter
public class Child {

    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;

    @Id
    @Column(name = "CHILD_ID")
    private String childId;

//    @EmbeddedId
//    private ChildId id;
//
//    @MapsId("parentId") //ChildId.parentId 매핑
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "PARENT_ID")
//    private Parent parent;

    private String name;
}
