package de.retest.ui.descriptors;

import static de.retest.util.RetestIdUtil.cut;
import static de.retest.util.RetestIdUtil.normalize;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultRetestIdProvider implements RetestIdProvider {

	private final Set<String> knownRetestIds = new HashSet<String>();

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		if ( identifyingAttributes == null ) {
			throw new NullPointerException( "IdentifyingAttributes must not be null." );
		}

		final String text = identifyingAttributes.get( "text" );
		final String type = identifyingAttributes.get( "type" );
		final String rawId = text != null ? text : type;
		return makeUnique( cut( normalize( rawId ) ) );
	}

	private String makeUnique( final String input ) {
		String unique = input;
		while ( knownRetestIds.contains( unique ) ) {
			unique = input + "-" + UUID.randomUUID().toString().substring( 0, 5 );
		}
		knownRetestIds.add( unique );
		return unique;
	}

	@Override
	public void reset() {
		knownRetestIds.clear();
	}

}
