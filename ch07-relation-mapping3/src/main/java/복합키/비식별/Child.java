package 복합키.비식별;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Child {

    @Id
    private String id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1",
                    referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2",
                    referencedColumnName = "PARENT_ID2")
    })
    private Parent parent;

}
