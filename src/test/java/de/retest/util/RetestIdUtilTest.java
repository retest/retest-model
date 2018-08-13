package de.retest.util;

import static de.retest.util.RetestIdUtil.normalize;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class RetestIdUtilTest {

	@Test
	public void weired_chars_should_be_removed_when_normalized() {
		assertThat( normalize( " this. (should) \n be -/ \\ +NORMALIZEDé{}" ) )
				.isEqualTo( "this_should_be_normalized" );
	}

	@Test
	public void too_long_text_should_be_cut_sensibly() {
		final String retestId = RetestIdUtil.cut( RetestIdUtil.normalize(
				"This       is\tsome     very long_sentence, \nthat    could be in a link text, or in some paragraph, and really is to long to be used as id." ) );
		assertThat( retestId.length() ).isLessThan( 20 );
		assertThat( retestId ).isEqualTo( "this_is_some" );
	}

	@Test
	public void too_long_words_should_be_cut() {
		final String retestId = RetestIdUtil.cut( "supercalifragilisticexpialidocious" );
		assertThat( retestId.length() ).isLessThan( 20 );
		assertThat( retestId ).isEqualTo( "supercalifragil" );
	}
}
