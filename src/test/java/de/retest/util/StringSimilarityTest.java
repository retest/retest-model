package de.retest.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.quickcheck.ExtendibleGenerator;
import net.java.quickcheck.Generator;
import net.java.quickcheck.QuickCheck;
import net.java.quickcheck.characteristic.AbstractCharacteristic;
import net.java.quickcheck.collection.Pair;
import net.java.quickcheck.generator.CombinedGeneratorsIterables;
import net.java.quickcheck.generator.PrimitiveGenerators;

public class StringSimilarityTest {

	private static final Logger logger = LoggerFactory.getLogger( StringSimilarityTest.class );

	@Test
	public void small_change_in_short_string_should_result_in_bigger_difference() {
		final String s0 = "abc";
		final String s1 = "adc";
		final String s2 = "abcd";
		final String s3 = "adcd";

		final double similarityPath0And1 = StringSimilarity.textSimilarity( s0, s1 );
		final double similarityPath2And3 = StringSimilarity.textSimilarity( s2, s3 );

		assertThat( similarityPath0And1 ).isCloseTo( 0.08, within( 0.01 ) );
		assertThat( similarityPath2And3 ).isCloseTo( 0.125, within( 0.01 ) );
	}

	@Test
	public void pathSimilarity_should_be_percentage_of_difference() {
		final String commonPrefix =
				"Window[1]/JRootPane[1]/JLayeredPane[1]/JPanel[1]/TabbedPane[1]/Tab[2]/ExtendedPanel[1]/JXPanel[1]/JXLayer[1]/Splitpane[1]/JXLayer[1]/JXPanel[1]/JXLayer[1]/TabbedModuleForm[1]/TabbedPaneExt[1]/Tab[1]/FormAdapter[1]/JPanel[1]/GUI_BE[1]";
		final String commonSuffix = "/Betriebename[1]/JXPanel[1]";
		final String difference = "_07_01_Betriebe_Stammdaten[1]";
		final String path0 = commonPrefix + difference + commonSuffix;
		final String path1 = commonPrefix + commonSuffix;

		final double similarity = StringSimilarity.pathSimilarity( path0, path1 );

		final double expectedSimilarity = (commonPrefix.length() + commonSuffix.length())
				/ (double) (commonPrefix.length() + commonSuffix.length() + difference.length());
		assertThat( similarity ).isCloseTo( expectedSimilarity * expectedSimilarity, within( 0.01 ) );
	}

	@Test
	public void pathSimilarity_should_be_between_0_and_1() {
		final ExtendibleGenerator<Character, String> pathGenerator = createPathGenerator();

		for ( final Pair<String, String> randomPathPair : CombinedGeneratorsIterables.somePairs( pathGenerator,
				pathGenerator ) ) {
			final String path0 = randomPathPair.getFirst();
			final String path1 = randomPathPair.getSecond();

			final double similarity01 = StringSimilarity.pathSimilarity( path0, path1 );
			final double similarity10 = StringSimilarity.pathSimilarity( path1, path0 );

			assertThat( similarity01 ).as( path0 + " compared to " + path1 ).isBetween( 0.0, 1.0 );
			assertThat( similarity10 ).as( path1 + " compared to " + path0 ).isBetween( 0.0, 1.0 );
		}
	}

	@Test
	public void pathSimilarity_for_equal_paths_should_always_return_1() throws Exception {
		QuickCheck.forAll( createPathGenerator(), new AbstractCharacteristic<String>() {
			@Override
			protected void doSpecify( final String randomPath ) throws Throwable {
				assertThat( StringSimilarity.pathSimilarity( randomPath, randomPath ) )
						.as( randomPath + " compared to itself" ).isEqualTo( 1.0 );
			}
		} );
	}

	private ExtendibleGenerator<Character, String> createPathGenerator() {
		final String pathAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_/";
		final Generator<Character> charGenerator = PrimitiveGenerators.characters( pathAlphabet );
		final ExtendibleGenerator<Character, String> pathGenerator = PrimitiveGenerators.strings( charGenerator );
		return pathGenerator;
	}

	@Test
	public void pathSimilarity_should_handle_null_values() throws Exception {
		final String s0 = null;
		final String s1 = "a";

		final double similarity00 = StringSimilarity.pathSimilarity( s0, s0 );
		final double similarity01 = StringSimilarity.pathSimilarity( s0, s1 );
		final double similarity10 = StringSimilarity.pathSimilarity( s1, s0 );

		assertThat( similarity00 ).isEqualTo( 1.0 );
		assertThat( similarity01 ).isEqualTo( 0.0 );
		assertThat( similarity10 ).isEqualTo( 0.0 );
	}

	@Test
	public void one_change_should_be_more_similar_than_two_changes() throws Exception {
		final String orig =
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[1]/JPanel[1]/JTable[1]/row[3]/column[5]";
		final String oneChange =
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[2]/JPanel[1]/JTable[1]/row[3]/column[5]";
		final String twoChanges =
				"Window[1]/JRootPane[1]/JLayeredPane[1]/StatefulTableDemo[1]/JTabbedPane[1]/Tab[2]/JPanel[1]/JTable[1]/row[3]/column[1]";

		final double similarityWithOneChange = StringSimilarity.pathSimilarity( orig, oneChange );
		final double similarityWithTwoChanges = StringSimilarity.pathSimilarity( orig, twoChanges );

		assertThat( similarityWithOneChange ).isGreaterThan( similarityWithTwoChanges );
	}

	@Test
	public void pathSimilarity_should_handle_common_prefix_and_suffix() throws Exception {
		final String s0 = "a";
		final String s1 = "aa";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	public void pathSimilarity_should_handle_common_prefix() throws Exception {
		final String s0 = "a";
		final String s1 = "ab";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	public void pathSimilarity_should_handle_common_suffix() throws Exception {
		final String s0 = "b";
		final String s1 = "ab";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isCloseTo( 0.5 * 0.5, within( 0.01 ) );
	}

	@Test
	public void pathSimilarity_for_completely_different_paths_should_return_0() throws Exception {
		final String s0 = "a";
		final String s1 = "b";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isEqualTo( 0.0 );
	}

	@Test
	public void pathSimilarity_for_completely_different_paths_should_return_0_ignoring_suffig() throws Exception {
		final String s0 = "a[1]";
		final String s1 = "b[1]";

		final double similarity = StringSimilarity.pathSimilarity( s0, s1 );

		assertThat( similarity ).isEqualTo( 0.0 );
	}

	@Test
	@Ignore( "This test can be performed manually to test other similarity implementations." )
	public void pathSimilarity_should_be_efficient() {
		// Measurements taken on Jeremy's machine (2,5 GHz Intel Core i7, 16 GB 1600 MHz DDR3).
		// for StringUtils.getLevenshteinDistance( path0, path1, (int) maxLength / 2 ): 112595ms
		// for StringUtils.getLevenshteinDistance( path0, path1, (int) minLength / 2 ): 104451ms
		// for StringUtils.getJaroWinklerDistance( path0, path1 ): 78468ms
		// for StringUtils.getFuzzyDistance( path0, path1, Locale.GERMAN ): 3344ms
		// for pathSimilarity(path0, path1): 922ms
		final String path0 =
				"Window/JRootPane[1]/JLayeredPane[1]/JPanel[1]/TabbedPane[1]/Tab_1/ExtendedPanel[1]/JXPanel[1]/JXLayer[1]/Splitpane[1]/JXLayer[1]/JXPanel[1]/JXLayer[1]/TabbedModuleForm[1]/TabbedPaneExt[1]/Tab[1]/FormAdapter[1]/JPanel[1]/GUI_BE_07_01_Betriebe_Stammdaten[1]/Betriebename[1]/JXPanel[1]";
		final String path1 =
				"Window/JRootPane[1]/JLayeredPane[1]/JPanel[1]/TabbedPane[1]/Tab_1/ExtendedPanel[1]/JXPanel[1]/JXLayer[1]/Splitpane[1]/JXLayer[1]/JXPanel[1]/JXLayer[1]/TabbedModuleForm[1]/TabbedPaneExt[1]/Tab[1]/FormAdapter[1]/JPanel[1]/GUI_BE/Betriebename[1]/JXPanel[1] ";

		logger.info( "Starting performance test..." );
		final long start = System.currentTimeMillis();
		for ( int index = 0; index < 1000000; index++ ) {
			StringSimilarity.pathSimilarity( path0, path1 );
		}
		logger.info( "Took {} ms.", System.currentTimeMillis() - start );
	}

}
