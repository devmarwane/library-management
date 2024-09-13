package business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 */
final public class Book implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 6110690276685962829L;
	private BookCopy[] copies;
	private List<Author> authors;
	private String isbn;
	private String title;
	private int maxCheckoutLength;
	public Book(String isbn, String title, int maxCheckoutLength, List<Author> authors) {
		this.isbn = isbn;
		this.title = title;
		this.maxCheckoutLength = maxCheckoutLength;
		this.authors = Collections.unmodifiableList(authors);
		copies = new BookCopy[]{new BookCopy(this, 1, true)};	
	}

	public Book(String isbn, String title, int maxCheckoutLength, List<Author> authors, BookCopy[] copies) {
		this.isbn = isbn;
		this.title = title;
		this.maxCheckoutLength = maxCheckoutLength;
		this.authors = Collections.unmodifiableList(authors);
		this.copies = copies;
	}
	
	public void updateCopies(BookCopy copy) {
		for(int i = 0; i < copies.length; ++i) {
			BookCopy c = copies[i];
			if(c.equals(copy)) {
				copies[i] = copy;
				
			}
		}
	}

	public List<Integer> getCopyNums() {
		List<Integer> retVal = new ArrayList<>();
		for(BookCopy c : copies) {
			retVal.add(c.getCopyNum());
		}
		return retVal;
		
	}

	public BookCopy addCopy() {
		// Find the maximum copy number in the current array of copies
		int maxCopyNum = 0;
		for (BookCopy copy : copies) {
			if (copy.getCopyNum() > maxCopyNum) {
				maxCopyNum = copy.getCopyNum();
			}
		}

		// Create a new array with an additional slot for the new copy
		BookCopy[] newArr = new BookCopy[copies.length + 1];
		System.arraycopy(copies, 0, newArr, 0, copies.length);

		// Add the new copy with copy number maxCopyNum + 1
		newArr[copies.length] = new BookCopy(this, maxCopyNum + 1, true);
		copies = newArr;

		// Return the newly added copy
		return copies[copies.length - 1];
	}


	public void removeCopy(BookCopy copy) {
		BookCopy[] newCopies = new BookCopy[copies.length - 1];
		int index = 0;
		for (BookCopy c : copies) {
			if (!c.equals(copy)) {
				newCopies[index++] = c;
			}
		}
		copies = newCopies;
	}
	
	
	@Override
	public boolean equals(Object ob) {
		if(ob == null) return false;
		if(ob.getClass() != getClass()) return false;
		Book b = (Book)ob;
		return b.isbn.equals(isbn);
	}
	
	
	public boolean isAvailable() {
		if(copies == null) {
			return false;
		}
		return Arrays.stream(copies)
				     .map(l -> l.isAvailable())
				     .reduce(false, (x,y) -> x || y);
	}
	@Override
	public String toString() {
		return "isbn: " + isbn + ", maxLength: " + maxCheckoutLength + ", available: " + isAvailable();
	}
	
	public int getNumCopies() {
		return copies.length;
	}

	public int getAvailableCopies() {
		int availableCount = 0;
		for (BookCopy copy : copies) {
			if (copy.isAvailable()) {
				availableCount++;
			}
		}
		return availableCount;
	}
	
	public String getTitle() {
		return title;
	}

	public BookCopy[] getCopies() {
		return copies;
	}
	
	public List<Author> getAuthors() {
		return authors;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public BookCopy getNextAvailableCopy() {	
		Optional<BookCopy> optional 
			= Arrays.stream(copies)
			        .filter(x -> x.isAvailable()).findFirst();
		return optional.isPresent() ? optional.get() : null;
	}
	
	public BookCopy getCopy(int copyNum) {
		for(BookCopy c : copies) {
			if(copyNum == c.getCopyNum()) {
				return c;
			}
		}
		return null;
	}
	public int getMaxCheckoutLength() {
		return maxCheckoutLength;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
