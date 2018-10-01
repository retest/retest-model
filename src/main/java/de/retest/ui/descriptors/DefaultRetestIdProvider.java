package de.retest.ui.descriptors;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import de.retest.util.RetestIdUtil;

public class DefaultRetestIdProvider implements RetestIdProvider {

	private static final String DELIMITER = "-";
	private final Set<String> knownRetestIds = new HashSet<>();

	@Override
	public String getRetestId( final IdentifyingAttributes identifyingAttributes ) {
		if ( identifyingAttributes == null ) {
			throw new NullPointerException( "Identifying attributes must not be null." );
		}
		final String text = identifyingAttributes.get( "text" );
		final String type = identifyingAttributes.get( "type" );
		final String rawId = StringUtils.isNotBlank( text ) ? text : type;
		final String id = StringUtils.isNotBlank( rawId ) ? RetestIdUtil.normalizeAndCut( rawId ) : getUniqueSuffix();
		return id.isEmpty() ? makeUnique( "component_id" ) : makeUnique( id );
	}

	private String makeUnique( final String id ) {
		String uniqueId = id;
		while ( knownRetestIds.contains( uniqueId ) ) {
			uniqueId = id + DELIMITER + getUniqueSuffix();
		}
		knownRetestIds.add( uniqueId );
		return uniqueId;
	}

	private String getUniqueSuffix() {
		return UUID.randomUUID().toString().substring( 0, 5 );
	}

	@Override
	public void reset() {
		knownRetestIds.clear();
	}

}
