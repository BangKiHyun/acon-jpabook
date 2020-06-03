package 다대다.복합키;

import java.io.Serializable;
import java.util.Objects;

public class MemberProductId implements Serializable {

    private String member; //MemberProduct.member와 연결
    private String product; //MemberProduct.product와 연결

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberProductId)) return false;
        MemberProductId that = (MemberProductId) o;
        return Objects.equals(member, that.member) &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, product);
    }
}
