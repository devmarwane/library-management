package business;

import javax.swing.*;
import java.time.LocalDate;

public final class CheckoutEntry {
    private  BookCopy bookcopy;
    private  LocalDate chekoutDate;
    private  LocalDate dueDate;

    CheckoutEntry( BookCopy bookcopy, LocalDate chekoutDate ) {
        this.bookcopy = bookcopy;
        this.chekoutDate = chekoutDate;
        this.dueDate = chekoutDate.plusDays(bookcopy.getBook().getMaxCheckoutLength());
    }
    public BookCopy getBookcopy() {
        return bookcopy;
    }

    public LocalDate getChekoutDate() {
        return chekoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String toString() {
        return bookcopy.toString() + "\t" + chekoutDate.toString() + "\t" + dueDate.toString();
    }

}
