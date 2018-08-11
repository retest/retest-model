package de.retest.util;

public class StringUtil {

	public static String normalizeString( String text ) {
		// replace all whitespace chars
		text = text.trim().replaceAll( "[\\s]", "_" );
		// remove all chars but a-z in any case, 0-9 or _
		text = text.replaceAll( "[^\\w]", "" );
		// remove long blanks
		while ( text.contains( "__" ) ) {
			text = text.replaceAll( "__", "_" );
		}
		return text.toLowerCase();
	}

	public static String cut( final String text ) {
		if ( text.length() <= 17 ) {
			return text;
		}
		int blank = text.indexOf( "_" );
		while ( blank < 12 ) {
			final int nextBlank = text.indexOf( "_", blank + 1 );
			if ( nextBlank < 17 && nextBlank > -1 ) {
				blank = nextBlank;
			} else {
				break;
			}
		}
		if ( blank >= 12 && blank <= 17 ) {
			// if possible, use first word
			return text.substring( 0, blank );
		}
		return text.substring( 0, 15 );
	}
}
