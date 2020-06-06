package 조인테이블;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "BOARD")
@SecondaryTable(name = "BOARD_DETAIL",
pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAIL_ID"))
@NoArgsConstructor
@Getter
public class Board {

    @Id @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    private String title;

    @Column(table = "BOARD_DETAIL")
    private String content;

}
