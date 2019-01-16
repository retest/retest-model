package de.retest.ui.diff;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class AttributesDifference implements Difference {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private final String differenceId;

	@XmlElement
	private final List<AttributeDifference> differences;

	@SuppressWarnings( "unused" )
	private AttributesDifference() {
		// for JAXB
		differenceId = null;
		differences = null;
	}

	AttributesDifference( final List<AttributeDifference> differences ) {
		this.differences = Collections.unmodifiableList( differences );
		differenceId = AttributeDifference.getSumIdentifier( differences );
	}

	@Override
	public String toString() {
		return "{" + differences.stream().map( AttributeDifference::toString ).collect( Collectors.joining( ", " ) )
				+ "}";
	}

	@Override
	public int size() {
		return differences.size();
	}

	@Override
	public List<ElementDifference> getNonEmptyDifferences() {
		return Collections.emptyList();
	}

	@Override
	public List<ElementDifference> getElementDifferences() {
		return Collections.emptyList();
	}

	public Map<String, Serializable> expected() {
		final Map<String, Serializable> expected = new HashMap<>();
		for ( final AttributeDifference difference : differences ) {
			expected.put( difference.getKey(), difference.getExpected() );
		}
		return expected;
	}

	public Map<String, Serializable> actual() {
		final Map<String, Serializable> actual = new HashMap<>();
		for ( final AttributeDifference difference : differences ) {
			actual.put( difference.getKey(), difference.getActual() );
		}
		return actual;
	}

	public List<AttributeDifference> getAttributes() {
		return differences;
	}

	public String getIdentifier() {
		return differenceId;
	}
}
