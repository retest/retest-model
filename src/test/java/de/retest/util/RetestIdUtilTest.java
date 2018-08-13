package de.retest.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class RetestIdUtilTest {

	@Test
	public void weired_chars_should_be_removed_when_normalized() {
		final String rawId = " this. (should) \n be -/ \\ +NORMALIZEDé{}";
		final String normalizedId = RetestIdUtil.normalize( rawId );
		assertThat( normalizedId ).isEqualTo( "this_should_be_normalized" );
	}

	@Test
	public void too_long_single_word_ids_should_be_cut() {
		final String rawId = "supercalifragilisticexpialidocious";
		final String cutId = RetestIdUtil.cut( rawId );
		assertThat( cutId.length() ).isLessThan( 20 );
		assertThat( cutId ).isEqualTo( "supercalifragil" );
	}

	@Test
	public void too_long_multi_word_ids_should_be_normalized_and_cut_sensibly() {
		final String rawId =
				"This       is\tsome     very long_sentence, \nthat    could be in a link text, or in some paragraph, and really is to long to be used as id.";
		final String normalizedAndCutId = RetestIdUtil.normalizeAndCut( rawId );
		assertThat( normalizedAndCutId.length() ).isLessThan( 20 );
		assertThat( normalizedAndCutId ).isEqualTo( "this_is_some" );
	}

}
