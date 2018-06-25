package de.retest.ui.descriptors;

import static de.retest.ui.descriptors.StringAttribute.ParameterTypeBoolean;
import static de.retest.ui.descriptors.StringAttribute.ParameterTypeClass;
import static de.retest.ui.descriptors.StringAttribute.ParameterTypeInteger;
import static de.retest.ui.descriptors.StringAttribute.ParameterTypeString;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringAttributeTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void should_parse_Boolean_values_correctly() throws Exception {
		assertThat( (Boolean) ParameterTypeBoolean.parse( "TRUE" ) ).isTrue();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "True" ) ).isTrue();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "true" ) ).isTrue();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "TrUe" ) ).isTrue();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "FALSE" ) ).isFalse();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "False" ) ).isFalse();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "false" ) ).isFalse();
		assertThat( (Boolean) ParameterTypeBoolean.parse( "fAlSe" ) ).isFalse();
	}

	@Test
	public void parse_should_throw_error_if_not_valid() throws Exception {
		exception.expect( ParameterParseException.class );
		exception.expectMessage( "Value must be 'true' or 'false' (ignoring case)." );

		ParameterTypeBoolean.parse( "not a boolean value" );
	}

	@Test
	public void should_parse_Class_values_correctly() throws Exception {
		assertThat( ParameterTypeClass.parse( "java.lang.Integer" ) ).isEqualTo( Integer.class );
		assertThat( ParameterTypeClass.parse( "de.retest.ui.descriptors.StringAttributeTest" ) )
				.isEqualTo( getClass() );
	}

	@Test( expected = ParameterParseException.class )
	public void parse_should_throw_class_not_found() throws Exception {
		assertThat( ParameterTypeClass.parse( "this.is.a.non.existent.Class" ) );
	}

	@Test
	public void should_return_String_values_as_is() throws Exception {
		assertThat( ParameterTypeString.parse( "Hi" ) ).isEqualTo( "Hi" );
		assertThat( ParameterTypeString.parse( "42" ) ).isEqualTo( "42" );
		assertThat( ParameterTypeString.parse( "true" ) ).isEqualTo( "true" );
	}

	@Test
	public void should_parse_Integer_correctly() throws Exception {
		assertThat( ParameterTypeInteger.parse( "42" ) ).isEqualTo( 42 );
		assertThat( ParameterTypeInteger.parse( "4711" ) ).isEqualTo( 4711 );
		assertThat( ParameterTypeInteger.parse( "0815" ) ).isEqualTo( 815 );
	}

	@Test( expected = ParameterParseException.class )
	public void parse_should_throw_exception() throws Exception {
		ParameterTypeInteger.parse( "test" );
	}
}
