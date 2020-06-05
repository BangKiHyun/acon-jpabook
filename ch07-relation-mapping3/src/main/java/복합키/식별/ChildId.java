package 복합키.식별;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
public class ChildId implements Serializable {

    private String parent;
    private String childId;

//    private String parentId; //@MapsId("parentId")로 매핑
//
//    @Column(name = "CHILD_ID")
//    private String id;

    public ChildId(String parent, String childId) {
        this.parent = parent;
        this.childId = childId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChildId)) return false;
        ChildId childId1 = (ChildId) o;
        return Objects.equals(parent, childId1.parent) &&
                Objects.equals(childId, childId1.childId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, childId);
    }
}
