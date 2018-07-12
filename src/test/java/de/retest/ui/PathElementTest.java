package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathElementTest {

	@Test
	public void string_representation() throws Exception {
		assertThat( new PathElement( "element", "suffix" ).toString() ).isEqualTo( "element[suffix]" );
		assertThat( new PathElement( "element", "" ).toString() ).isEqualTo( "element" );
		assertThat( new PathElement( "", "suffix" ).toString() ).isEqualTo( "[suffix]" );
		assertThat( new PathElement( "element" ).toString() ).isEqualTo( "element" );
		assertThat( new PathElement().toString() ).isEqualTo( "" );
	}

}
