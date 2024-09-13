package business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRecord {
    private LibraryMember member;
    private List<CheckoutEntry> entries;

    CheckoutRecord(LibraryMember member){
        this.member = member;
        entries = new ArrayList<>();
    }
    public BookCopy addCheckoutEntry(BookCopy bc){
        CheckoutEntry e = new CheckoutEntry(bc,LocalDate.now());
        entries.add(e);
        return bc;
    }
}
