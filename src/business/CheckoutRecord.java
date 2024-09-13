package business;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRecord implements Serializable {
    private LibraryMember member;
    private List<CheckoutEntry> entries;

    CheckoutRecord(LibraryMember member){
        this.member = member;
        entries = new ArrayList<>();
    }
    public CheckoutEntry addCheckoutEntry(BookCopy bc){
        CheckoutEntry e = new CheckoutEntry(bc,member,LocalDate.now());
        entries.add(e);
        return e;
    }
    public CheckoutEntry getLastEntry(){
        return entries.getLast();
    }
    public List<CheckoutEntry> getEntries(){
        return entries;
    }
}
