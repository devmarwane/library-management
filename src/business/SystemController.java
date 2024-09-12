package business;

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
	private List<LibraryMember> members;

	public SystemController (){
		DataAccess da = new DataAccessFacade();
		userMap = da.readUserMap();
		allAuthors();
		//@todo fill members
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
		List<Book> retval = new ArrayList<>();
		retval.addAll(da.readBooksMap().values());
		return retval;
	}

	@Override
	public List<LibraryMember> allMembers() {
		DataAccess da = new DataAccessFacade();
		List<LibraryMember> retval = new ArrayList<>();
		retval.addAll(da.readMemberMap().values());
		return retval;
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

	public CheckoutEntry checkoutBook(String isbn, String memberId) throws LibrarySystemException {
		//@todo
		BookCopy copy = getCopyofBook(isbn);
		LibraryMember mem = getMemberRecord(memberId);
		//addCheckoutEnty(copy,mem);
		//saveEntry();
		CheckoutEntry e = new CheckoutEntry();
		//return CheckoutEntry;
		return null;
	}
	private LibraryMember getMemberRecord(String memberid){
		return null;
	}

	private BookCopy getCopyofBook(String isbn) throws LibrarySystemException {

		//BookCopy copy = new BookCopy();
		// @todo look for a copy in allBooks list
		//return copy;
		return null;
	}
	private void addCheckoutEnty(BookCopy bc, LibraryMember mem){

	}
}
