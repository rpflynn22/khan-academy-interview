
public class Person {
	/**
	 * Creates a new person.
	 * @param name    The name of the person.
	 * @param id      The person's unique identifier.
	 * @param version The version of the site this person is set to see.
	 */
	public Person(String name, String id) {
		_name = name;
		_id = id;
		_newVersion = false;;
	}
	
	/**
	 * Getter method for the person's name. 
	 * @return this person's name.
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Getter method for the person's id.
	 * @return this person's unique identifier.	
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * Getter for the version this person sees.
	 * @return True if the person is on the new version. False otherwise.
	 */
	public boolean getVersion() {
		return _newVersion;
	}
	
	/**
	 * Changes new version field from false to true.
	 */
	public void updateVersion() {
		_newVersion = true;
	}
	
	/** String representing the person's name. */
	private String _name;
	/** String representing the user's unique identifier (ip or something). */
	private String _id;
	/** String representation of the version of the site the user sees. */
	private boolean _newVersion;
}
