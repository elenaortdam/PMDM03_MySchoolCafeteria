package com.iesribera.myschoolcafeteria;

public class User {

	private static final boolean allowNewInstance = true;

	private String name;
	private String email;

/*	private static User instance = null;

	public User(String name, String email) throws Exception {
		if (!allowNewInstance)
			throw new Exception("No se puede crear la instancia, usa getInstance");
		else {
			this.name = name;
			this.email = email;
			instance = new User(name, email);
			allowNewInstance = false;
		}
	}

 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	private String uid;

	private static User mInstance;

	//nobody can instantiate
	private User() {

	}

	public static User getInstance() {
		if (mInstance == null)
			mInstance = new User();
		return mInstance;
	}
}
