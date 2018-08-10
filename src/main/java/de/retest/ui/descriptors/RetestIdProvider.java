package de.retest.ui.descriptors;

/**
 * This interface can be implemented by customers, to configure how the retest Id is created from given identifying
 * attributes. E.g. some might prefer a combination of class name and text, or id, or path, or some completely random
 * value, like UUID.
 */
public interface RetestIdProvider {

	public static final String ID_PROVIDER_CONFIGURATION_PROPERTY = "de.retest.RetestIdProvider";

	/**
	 * Get a retest Id for the given identifying attributes. Ideally, this should be human readable or have some
	 * intuitive relation to the given identifying attributes.
	 *
	 * But it absolutely <em>must</em> be unique within a given state. The {@link #reset()} method is used to in between
	 * states.
	 */
	String getRetestId( IdentifyingAttributes identifyingAttributes );

	/**
	 * Resets the state, such that the same retest id can be used after calling this method.
	 */
	void reset();

}
