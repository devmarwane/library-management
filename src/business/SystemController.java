package business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataaccess.Auth;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;
import dataaccess.User;

public class SystemController implements ControllerInterface {
	private Auth currentAuth;
	private HashMap<String, User> userMap;
	private List<Author> authors;
	private List<LibraryMember> allMembers;
	private List<Book> allBooks;
	private DataAccess da;

	public SystemController (){
		da = new DataAccessFacade();
		userMap = da.readUserMap();
		allAuthors();
		allMembers();
	}
	public void login(String id, String password) throws LoginException {
		if(!userMap.containsKey(id)) {
			throw new LoginException("ID " + id + " not found");
		}
		String passwordFound = userMap.get(id).getPassword();
		if(!passwordFound.equals(password)) {
			throw new LoginException("Password incorrect");
		}
		currentAuth = userMap.get(id).getAuthorization();
	}

	public Auth getCurrentAuth() {
		return currentAuth;
	}

	@Override
	public List<String> allMemberIds() {
		DataAccess da = new DataAccessFacade();
		List<String> retval = new ArrayList<>();
		retval.addAll(da.readMemberMap().keySet());
		return retval;
	}
	
	@Override
	public List<String> allBookIds() {
		DataAccess da = new DataAccessFacade();
		List<String> retval = new ArrayList<>();
		retval.addAll(da.readBooksMap().keySet());
		return retval;
	}

	@Override
	public List<Book> allBooks() {
		DataAccess da = new DataAccessFacade();
		allBooks = new ArrayList<>();
		allBooks.addAll(da.readBooksMap().values());
		return allBooks;
	}

	@Override
	public List<LibraryMember> allMembers() {
		DataAccess da = new DataAccessFacade();
		allMembers = new ArrayList<>();
		allMembers.addAll(da.readMemberMap().values());
		return allMembers;
	}

	@Override
	public List<Author> allAuthors() {
		DataAccess da = new DataAccessFacade();
		List<Book> retval = new ArrayList<>();
		retval.addAll(da.readBooksMap().values());
		authors= retval.stream().flatMap(x->x.getAuthors().stream()).toList();
		return authors;
	}

	public static void main(String[] args){
		SystemController a = new SystemController();

		DataAccess da = new DataAccessFacade();

		System.out.println(da.readMemberMap().values());
		System.out.println(a.allAuthors());
	}

	public CheckoutEntry checkoutBook( String memberId, String isbn) throws LibrarySystemException {
		//@todo
		BookCopy copy = getCopyofBook(isbn);
		LibraryMember mem = getMemberRecord(memberId);
		CheckoutRecord checkoutRecord = mem.getCheckoutRecord();
		checkoutRecord.addCheckoutEntry(copy);
		//saveEntry();

		//return CheckoutEntry;
		return null;
	}

	private LibraryMember getMemberRecord(String memberId) throws LibrarySystemException {
		DataAccess da = new DataAccessFacade();
		HashMap<String, LibraryMember> mems = da.readMemberMap();
		LibraryMember member = mems.get(memberId);
		if (member != null){
			return member;
		} else {
			throw new LibrarySystemException("No Member");
		}
	}

	private BookCopy getCopyofBook(String isbn) throws LibrarySystemException {
		HashMap<String, Book> books = da.readBooksMap();
		Book book = books.get(isbn);

		if (book==null) {
			throw new LibrarySystemException("The ISBN you entered does not match any book in our system.");
		}

		if (!book.isAvailable()) {
			throw new LibrarySystemException("The selected book is unavailable right now.");
		}
		BookCopy bookCopy = book.getNextAvailableCopy();
		bookCopy.changeAvailability();

		da.updateBook(book);

		return bookCopy;
	}
}
