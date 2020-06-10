package jpabook.entity;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    private Date createDate;
    private Date alstModifiedDate;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getAlstModifiedDate() {
        return alstModifiedDate;
    }

    public void setAlstModifiedDate(Date alstModifiedDate) {
        this.alstModifiedDate = alstModifiedDate;
    }
}
