package 복합키.식별;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@IdClass(GrandChildId.class)
@NoArgsConstructor
@Getter
@Setter
public class GrandChild {

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;

    @Id
    @Column(name = "GRAND_CHILD_ID")
    private String id;

//    @EmbeddedId
//    private GrandChildId id;
//
//    @MapsId("childId")
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "PARENT_ID"),
//            @JoinColumn(name = "CHILD_ID")
//    })
//    private Child child;

    private String name;
}
