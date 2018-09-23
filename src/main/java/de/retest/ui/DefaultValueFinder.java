package de.retest.ui;

import java.io.Serializable;

import de.retest.ui.descriptors.IdentifyingAttributes;

public interface DefaultValueFinder {

	/**
	 * Returns a default value for the give StateAttributes (font, text color, background color, ...). Default values
	 * are not persisted for every element to save (enormous) space. But if the value is not default, we want to know
	 * what default would have been.
	 *
	 * @param identifyingAttributes
	 *            identifying attributes
	 * @param attributesKey
	 *            attributes key
	 * @return the default value for the given component and the given state attributes key or {@code null} if not
	 *         applicable or unknown
	 */
	Serializable getDefaultValue( final IdentifyingAttributes identifyingAttributes, final String attributesKey );
}
