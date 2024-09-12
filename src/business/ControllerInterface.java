package business;

import java.lang.reflect.Member;
import java.util.List;

import business.Book;
import dataaccess.Auth;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;

public interface ControllerInterface {
	public Auth getCurrentAuth();
	public void login(String id, String password) throws LoginException;
	public List<String> allMemberIds();
	public List<String> allBookIds();
	public List<Book> allBooks();
	public List<LibraryMember> allMembers();
	public List<Author> allAuthors();

	/**
	 *
	 * @param isbn isbn of the desired book
	 * @param memberId identifier of the Library Member
	 * @return Ckeckout record containing the copy of the book and the due date
	 * @throws LibrarySystemException if no copy is available or book or member do not exists
	 */
	public CheckoutEntry checkoutBook(String isbn, String memberId)  throws LibrarySystemException;


}
