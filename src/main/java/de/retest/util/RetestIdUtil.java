package de.retest.util;

public class RetestIdUtil {

	public static String normalizeAndCut( final String id ) {
		return cut( normalize( id ) );
	}

	static String normalize( String id ) {
		// trime and replace all whitespace chars with _
		id = id.trim().replaceAll( "[\\s]", "_" );
		// remove all chars but a-z in any case, 0-9 or _
		id = id.replaceAll( "[^\\w]", "" );
		// remove long blanks
		while ( id.contains( "__" ) ) {
			id = id.replaceAll( "__", "_" );
		}
		return id.toLowerCase();
	}

	static String cut( final String id ) {
		if ( id.length() <= 17 ) {
			return id;
		}
		int blank = id.indexOf( '_' );
		while ( blank < 12 ) {
			final int nextBlank = id.indexOf( '_', blank + 1 );
			if ( nextBlank < 17 && nextBlank > -1 ) {
				blank = nextBlank;
			} else {
				break;
			}
		}
		if ( blank >= 12 && blank <= 17 ) {
			// if possible, use first word
			return id.substring( 0, blank );
		}
		return id.substring( 0, 15 );
	}

}
