package business;

import java.io.Serializable;

final public class Author extends Person implements Serializable {
	private String bio;
	public String getBio() {
		return bio;
	}

	private boolean credentials;

	public boolean getCredentials() {
		return this.credentials;
	}

	public Author(String f, String l, String t, Address a, String bio, boolean credentials) {
		super(f, l, t, a);
		this.bio = bio;
		this.credentials = credentials;
	}

	private static final long serialVersionUID = 7508481940058530471L;

	public String toString() {
		return "Author : " + this.getFullName() + ", bio: " + getBio();
	}
}
