package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathElementTest {

	@Test
	public void string_representation() throws Exception {
		assertThat( new PathElement( "element", "suffix" ).toString() ).isEqualTo( "element[suffix]" );
		assertThat( new PathElement( "element", "" ).toString() ).isEqualTo( "element" );
		assertThat( new PathElement( "element" ).toString() ).isEqualTo( "element" );
	}

	@Test( expected = IllegalArgumentException.class )
	public void empty_path_element_throws_exception() {
		assertThat( new PathElement( "", "suffix" ) );
	}

	@Test( expected = NullPointerException.class )
	public void null_path_element_throws_exception() {
		assertThat( new PathElement( null, "suffix" ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void blank_path_element_throws_exception() {
		assertThat( new PathElement( " ", "suffix" ) );
	}

	@Test( expected = IllegalArgumentException.class )
	public void tab_path_element_throws_exception() {
		assertThat( new PathElement( "	", "suffix" ) );
	}

	@Test
	public void input_should_be_trimmed() {
		assertThat( new PathElement( "	element ", "suffix" ).toString() ).isEqualTo( "element[suffix]" );
		assertThat( new PathElement( "element ", "  " ).toString() ).isEqualTo( "element" );
		assertThat( new PathElement( "element ", " suffix " ).toString() ).isEqualTo( "element[suffix]" );
	}
}
