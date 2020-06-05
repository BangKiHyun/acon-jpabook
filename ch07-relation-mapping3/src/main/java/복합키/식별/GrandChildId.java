package 복합키.식별;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
public class GrandChildId implements Serializable {

    private ChildId childId;
    private String id;

//    private ChildId childId; //@Maps("childId")로 매핑
//
//    @Column(name = "GRAND_CHILD_ID")
//    private String id;

    public GrandChildId(ChildId childId, String id) {
        this.childId = childId;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrandChildId)) return false;
        GrandChildId that = (GrandChildId) o;
        return Objects.equals(childId, that.childId) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childId, id);
    }
}
