package de.retest.ui.descriptors;

import static de.retest.ui.Path.fromString;
import static de.retest.ui.descriptors.IdentifyingAttributes.create;
import static de.retest.ui.descriptors.IdentifyingAttributes.createList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

public class DefaultRetestIdProviderTest {

	@Test
	public void too_long_text_should_be_cut() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();

		// for what you would actually call a text
		final IdentifyingAttributes ident = createIdentAttributes(
				"This is some very long sentence, that could be in a link text, or in some paragraph, and really is to long to be used as id." );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId.length() ).isLessThan( 20 );

		// and cut should still be unique!
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	public void too_long_words_should_be_cut() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();

		// but also for single words
		final IdentifyingAttributes ident = createIdentAttributes( "supercalifragilisticexpialidocious" );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId.length() ).isLessThan( 20 );

		// and cut should still be unique!
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	private IdentifyingAttributes createIdentAttributes( final String text ) {
		final Collection<Attribute> attributes = createList( fromString( "/HTML/DIV[1]" ), "DIV" );
		attributes.add( new StringAttribute( "text", text ) );
		return new IdentifyingAttributes( attributes );
	}

	@Test
	public void should_always_be_unique() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();
		final IdentifyingAttributes ident = createIdentAttributes( "a" );
		final String retestId = cut.getRetestId( ident );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
		assertThat( retestId ).isNotEqualTo( cut.getRetestId( ident ) );
	}

	@Test
	public void works_even_only_for_path_and_type() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();
		final IdentifyingAttributes ident = create( fromString( "/HTML/DIV[1]" ), "DIV" );
		final String id1 = cut.getRetestId( ident );
		final String id2 = cut.getRetestId( ident );
		assertThat( id1 ).isNotEqualTo( id2 );
	}

	@Test
	public void no_text_should_give_type() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();
		final Collection<Attribute> attributes = createList( fromString( "/HTML/DIV[1]" ), "DIV" );
		attributes.add( new StringAttribute( "type", "DIV" ) );
		attributes.add( new SuffixAttribute( 3 ) );
		assertThat( cut.getRetestId( new IdentifyingAttributes( attributes ) ) ).isEqualTo( "div" );
	}

	@Test( expected = NullPointerException.class )
	public void null_should_give_exception() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();
		cut.getRetestId( null );
	}

	@Test( expected = NullPointerException.class )
	public void null_path_should_give_exception() {
		final DefaultRetestIdProvider cut = new DefaultRetestIdProvider();
		cut.getRetestId( create( null, "DIV" ) );
	}
}
