package de.retest.ui.descriptors;

import java.util.HashMap;
import java.util.Map;

import de.retest.util.ObjectUtil;

public abstract class ParameterType {

	private final String toString;

	public ParameterType( final String toString ) {
		this.toString = toString;
	}

	public abstract Object parse( final String value ) throws ParameterParseException;

	public boolean canParse( final String value ) {
		try {
			parse( value );
			return true;
		} catch ( final ParameterParseException e ) {
			return false;
		}
	}

	@Override
	public String toString() {
		return toString;
	}

	@Override
	public int hashCode() {
		return toString.hashCode();
	}

	@Override
	public boolean equals( final Object other ) {
		return ObjectUtil.equal( toString, other.toString() );
	}

	private static final Map<String, ParameterType> registeredParameterTypes = new HashMap<String, ParameterType>();

	public static void registerParameterType( final ParameterType type ) {
		registeredParameterTypes.put( type.toString, type );
	}

	public static ParameterType getType( final String type ) {
		final ParameterType parameterType = registeredParameterTypes.get( type );
		if ( parameterType != null ) {
			return parameterType;
		}
		// If this comes in a test: was the ParameterType registered?
		throw new IllegalStateException( "No ParameterType registered for parameters of type " + type );
	}

	public static void registerStdParameterTypes() {
		registerParameterType( PathAttribute.ParameterTypePath );
		registerParameterType( DefaultAttribute.ParameterTypeAttribute );
		registerParameterType( StringAttribute.ParameterTypeString );
		registerParameterType( StringAttribute.ParameterTypeBoolean );
		registerParameterType( StringAttribute.ParameterTypeInteger );
		registerParameterType( StringAttribute.ParameterTypeClass );
	}
}
