package business;

import java.io.Serializable;

final public class LibraryMember extends Person implements Serializable {
    private String memberId;
    private CheckoutRecord checkoutRecord;
    public LibraryMember(String memberId, String fname, String lname, String tel, Address add) {
        super(fname, lname, tel, add);
        this.memberId = memberId;
        this.checkoutRecord = new CheckoutRecord(this);
    }



    public String getMemberId() {
        return memberId;
    }

    public CheckoutRecord getCheckoutRecord() {
        if (checkoutRecord == null)
            checkoutRecord = new CheckoutRecord(this);
        return checkoutRecord;
    }
    public CheckoutEntry addCheckoutEntry(BookCopy bc) {
        if (checkoutRecord == null)
            checkoutRecord = new CheckoutRecord(this);
        return checkoutRecord.addCheckoutEntry(bc);
    }


    @Override
    public String toString() {
        return "Member Info: " + "ID: " + memberId + ", name: " + getFirstName() + " " + getLastName() +
                ", " + getTelephone() + " " + getAddress();
    }

    private static final long serialVersionUID = -2226197306790714013L;
}
