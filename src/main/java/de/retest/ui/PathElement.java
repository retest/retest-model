package de.retest.ui;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.retest.persistence.StringInternerAdapter;

@XmlAccessorType( XmlAccessType.FIELD )
public class PathElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	@XmlJavaTypeAdapter( StringInternerAdapter.class )
	private final String elementName;

	@XmlElement
	private final String suffix;

	public PathElement( final String elementName, final String suffix ) {
		if ( elementName == null ) {
			throw new NullPointerException( "Element name must not be null." );
		}
		this.elementName = elementName.trim();
		if ( this.elementName.isEmpty() ) {
			throw new IllegalArgumentException( "Element name must not be empty." );
		}
		this.suffix = suffix.trim();
	}

	public PathElement( final String elementName ) {
		this( elementName, "" );
	}

	public PathElement( final String elementName, final int suffix ) {
		this( elementName, "" + suffix );
	}

	public String getElementName() {
		return elementName;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		if ( "".equals( suffix ) ) {
			return elementName;
		} else {
			return elementName + "[" + suffix + "]";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (elementName == null ? 0 : elementName.hashCode());
		result = prime * result + (suffix == null ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final PathElement other = (PathElement) obj;
		if ( elementName == null ) {
			if ( other.elementName != null ) {
				return false;
			}
		} else if ( !elementName.equals( other.elementName ) ) {
			return false;
		}
		if ( suffix == null ) {
			if ( other.suffix != null ) {
				return false;
			}
		} else if ( !suffix.equals( other.suffix ) ) {
			return false;
		}
		return true;
	}

	public static PathElement fromString( final String path ) {
		if ( !path.contains( "[" ) ) {
			return new PathElement( path );
		}
		return new PathElement( path.substring( 0, path.indexOf( "[" ) ),
				path.substring( path.indexOf( "[" ) + 1, path.length() - 1 ) );
	}
}
