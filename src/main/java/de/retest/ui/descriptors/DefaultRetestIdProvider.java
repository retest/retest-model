package de.retest.ui.descriptors;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.retest.util.RetestIdUtil;

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
		return makeUnique( rawId );
	}

	private String makeUnique( final String rawId ) {
		final String id = RetestIdUtil.normalizeAndCut( rawId );
		String uniqueId = id;
		while ( knownRetestIds.contains( uniqueId ) ) {
			uniqueId = id + "-" + UUID.randomUUID().toString().substring( 0, 5 );
		}
		knownRetestIds.add( uniqueId );
		return uniqueId;
	}

	@Override
	public void reset() {
		knownRetestIds.clear();
	}

}
