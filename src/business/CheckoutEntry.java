package business;

import java.io.Serializable;
import java.time.LocalDate;

public final class CheckoutEntry implements Serializable {
    private  final BookCopy bookcopy;
    private  final LibraryMember member;
    private  final LocalDate checkoutDate;
    private  final LocalDate dueDate;

    CheckoutEntry( BookCopy bookcopy, LibraryMember member, LocalDate checkoutDate ) {
        this.member = member;
        this.bookcopy = bookcopy;
        this.checkoutDate = checkoutDate;
        this.dueDate = checkoutDate.plusDays(bookcopy.getBook().getMaxCheckoutLength());
    }
    public LibraryMember getMember() {
        return member;
    }
    public BookCopy getBookcopy() {
        return bookcopy;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String toString() {
        return bookcopy.toString() + "\t" + checkoutDate.toString() + "\t" + dueDate.toString();
    }

}
