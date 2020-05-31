package jpabook.start.domain;

import javax.persistence.*;

@Entity
@Table(name = "BOARD")
public class Board {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SEQ_AUTO",
    sequenceName = "DB_SEQ",
    allocationSize = 1)
    private Long id;

    @Column(name = "DATA")
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
