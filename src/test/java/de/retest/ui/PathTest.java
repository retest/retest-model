package de.retest.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathTest {

	private static final Path EMPTY_PATH = Path.path( new PathElement() );
	private static final PathElement EMPTY_ELEMENT = new PathElement();
	private static final PathElement ELEMENT_0 = new PathElement( "elem", "0" );
	private static final PathElement ELEMENT_1 = new PathElement( "leleme", "1" );
	private static final PathElement ELEMENT_2 = new PathElement( "bubi" );
	private static final PathElement ELEMENT_3 = new PathElement( "muma", "3" );

	@Test
	public void string_representation_of_empty_path() throws Exception {
		assertThat( EMPTY_PATH.toString() ).isEqualTo( "" );
		assertThat( Path.fromString( "" ) ).isEqualTo( EMPTY_PATH );
	}

	@Test
	public void string_representation_of_simple_path() throws Exception {
		assertThat( Path.path( ELEMENT_0 ).toString() ).isEqualTo( "elem_0" );
		assertThat( Path.fromString( "elem_0" ) ).isEqualTo( Path.path( ELEMENT_0 ) );
	}

	@Test
	public void string_representation_of_nested_path_with_empty_path() throws Exception {
		assertThat( Path.path( EMPTY_PATH, EMPTY_ELEMENT ).toString() ).isEqualTo( "/" );
		assertThat( Path.path( EMPTY_PATH, ELEMENT_0 ).toString() ).isEqualTo( "/elem_0" );
		assertThat( Path.fromString( "/" ) ).isEqualTo( Path.path( EMPTY_PATH, EMPTY_ELEMENT ) );
		assertThat( Path.fromString( "/elem_0" ) ).isEqualTo( Path.path( EMPTY_PATH, ELEMENT_0 ) );
	}

	@Test
	public void string_representation_of_nested_parent_path() throws Exception {
		assertThat( Path.path( Path.path( ELEMENT_0 ), ELEMENT_1 ).toString() ).isEqualTo( "elem_0/leleme_1" );
	}

	@Test
	public void string_representation_of_multilevel_nested_parent_path() throws Exception {
		final Path path0 = Path.path( ELEMENT_0 );
		final Path path1 = Path.path( path0, ELEMENT_1 );
		final Path path2 = Path.path( path1, ELEMENT_2 );
		final Path path3 = Path.path( path2, ELEMENT_3 );
		assertThat( path3.toString() ).isEqualTo( "elem_0/leleme_1/bubi/muma_3" );
		assertThat( Path.fromString( "elem_0/leleme_1/bubi/muma_3" ) ).isEqualTo( path3 );
	}

	@Test
	public void test_parentPath() {
		assertThat( EMPTY_PATH.isParent( EMPTY_PATH ) );
		final Path path1 = Path.path( Path.path( ELEMENT_0 ), ELEMENT_1 );
		assertThat( Path.path( ELEMENT_0 ).isParent( path1 ) );
		assertThat( path1.isParent( path1 ) );
		assertThat( !Path.path( ELEMENT_1 ).isParent( path1 ) );
	}
}
