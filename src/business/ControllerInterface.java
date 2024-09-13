package business;

import java.util.List;

import dataaccess.Auth;

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
	public LibraryMember checkoutBook( String memberId, String isbn) throws LibrarySystemException;
	public LibraryMember getMemberById(String memberId) throws LibrarySystemException;

}
