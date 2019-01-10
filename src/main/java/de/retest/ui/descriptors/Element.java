package de.retest.ui.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;

import de.retest.ui.Path;
import de.retest.ui.diff.AttributeDifference;
import de.retest.ui.image.Screenshot;
import de.retest.ui.review.ActionChangeSet;
import de.retest.util.RetestIdUtil;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Element implements Serializable, Comparable<Element> {

	protected static final long serialVersionUID = 2L;

	@XmlAttribute
	protected final String retestId;

	@XmlJavaTypeAdapter( IdentifyingAttributesAdapter.class )
	@XmlElement
	protected final IdentifyingAttributes identifyingAttributes;

	@XmlJavaTypeAdapter( StateAttributesAdapter.class )
	@XmlElement
	protected final Attributes attributes;

	// TODO Change filter method of IgnoreElements to make this an Collections.unmodifiableList.
	@XmlElement
	@XmlJavaTypeAdapter( RenderContainedElementsAdapter.class )
	//TODO Change containedComponents to containedElements
	protected final List<Element> containedComponents;

	@XmlElement
	protected Screenshot screenshot;

	@XmlTransient
	private transient Integer hashCodeCache;

	// Warning: Only to be used by JAXB!
	protected Element() {
		retestId = "";
		identifyingAttributes = null;
		attributes = null;
		containedComponents = new ArrayList<>();
	}

	Element( final String retestId, final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final List<Element> containedElements ) {
		RetestIdUtil.validate( retestId, identifyingAttributes );
		if ( identifyingAttributes == null ) {
			throw new NullPointerException( "IdentifyingAttributes must not be null." );
		}
		if ( attributes == null ) {
			throw new NullPointerException( "Attributes must not be null." );
		}
		this.retestId = retestId;
		this.identifyingAttributes = identifyingAttributes;
		this.attributes = attributes;
		containedComponents = containedElements;
	}

	public static Element withoutContainedElements( final String retestId,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes ) {
		return new Element( retestId, identifyingAttributes, attributes, new ArrayList<>() );
	}

	public static Element withContainedElements( final String retestId,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Element... containedElements ) {
		return new Element( retestId, identifyingAttributes, attributes, Arrays.asList( containedElements ) );
	}

	public static Element withContainedElements( final String retestId,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final List<Element> containedElements ) {
		return new Element( retestId, identifyingAttributes, attributes, containedElements );
	}

	public static Element withContainedElements( final String retestId,
			final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final List<Element> containedElements, final Screenshot screenshot ) {
		final Element element = new Element( retestId, identifyingAttributes, attributes, containedElements );
		element.setScreenshot( screenshot );
		return element;
	}

	public Element applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}

		final IdentifyingAttributes newIdentAttributes = identifyingAttributes
				.applyChanges( actionChangeSet.getIdentAttributeChanges().getAll( identifyingAttributes ) );

		final Attributes newAttributes =
				attributes.applyChanges( actionChangeSet.getAttributesChanges().getAll( identifyingAttributes ) );
		final List<Element> newContainedElements = createNewElementList( actionChangeSet, newIdentAttributes );

		return new Element( retestId, newIdentAttributes, newAttributes, newContainedElements );
	}

	protected List<Element> createNewElementList( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes ) {
		List<Element> newContainedElements = containedComponents;
		newContainedElements = removeDeleted( actionChangeSet, newContainedElements );
		newContainedElements =
				applyChangesToContainedElements( actionChangeSet, newIdentAttributes, newContainedElements );
		newContainedElements = addInserted( actionChangeSet, newIdentAttributes, newContainedElements );
		return newContainedElements;
	}

	private List<Element> removeDeleted( final ActionChangeSet actionChangeSet,
			final List<Element> oldContainedElements ) {
		final Set<IdentifyingAttributes> deletedChanges = actionChangeSet.getDeletedChanges();
		final List<Element> newContainedElements = new ArrayList<>( oldContainedElements.size() );

		for ( final Element oldElement : oldContainedElements ) {
			if ( !deletedChanges.contains( oldElement.getIdentifyingAttributes() ) ) {
				newContainedElements.add( oldElement );
			}
		}

		return newContainedElements;
	}

	private List<Element> applyChangesToContainedElements( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> oldContainedElements ) {
		final List<Element> newContainedElements = new ArrayList<>( oldContainedElements.size() );

		for ( final Element oldElement : oldContainedElements ) {
			addPathChangeToChangeSet( actionChangeSet, newIdentAttributes, oldElement );
			newContainedElements.add( oldElement.applyChanges( actionChangeSet ) );
		}

		return newContainedElements;
	}

	private void addPathChangeToChangeSet( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final Element oldElement ) {
		if ( ObjectUtils.notEqual( identifyingAttributes.getPathTyped(), newIdentAttributes.getPathTyped() ) ) {
			final Path oldPath = oldElement.identifyingAttributes.getPathTyped();
			final Path newPath = Path.fromString( newIdentAttributes.getPath() + Path.PATH_SEPARATOR
					+ oldElement.identifyingAttributes.getPathElement().toString() );

			actionChangeSet.getIdentAttributeChanges().add( oldElement.identifyingAttributes,
					new AttributeDifference( "path", oldPath, newPath ) );
		}
	}

	private List<Element> addInserted( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> newContainedElements ) {
		for ( final Element insertedElement : actionChangeSet.getInsertedChanges() ) {
			if ( isParent( newIdentAttributes, insertedElement.identifyingAttributes ) ) {
				newContainedElements.add( insertedElement );
			}
		}
		return newContainedElements;
	}

	private static boolean isParent( final IdentifyingAttributes parentIdentAttributes,
			final IdentifyingAttributes containedIdentAttributes ) {
		return parentIdentAttributes.getPathTyped().equals( containedIdentAttributes.getParentPathTyped() );
	}

	public int countAllContainedElements() {
		// count current element!
		int result = 1;
		for ( final Element element : containedComponents ) {
			result += element.countAllContainedElements();
		}
		return result;
	}

	public Element getElement( final Path path ) {
		final Path thisPath = getIdentifyingAttributes().getPathTyped();
		if ( thisPath.equals( path ) ) {
			return this;
		}
		if ( thisPath.isParent( path ) ) {
			for ( final Element element : containedComponents ) {
				final Element contained = element.getElement( path );
				if ( contained != null ) {
					return contained;
				}
			}
		}
		return null;
	}

	public IdentifyingAttributes getIdentifyingAttributes() {
		return identifyingAttributes;
	}

	public List<Element> getContainedElements() {
		return containedComponents;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public String getRetestId() {
		return retestId;
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

	public void setScreenshot( final Screenshot screenshot ) {
		if ( screenshot == null && this.screenshot != null ) {
			throw new RuntimeException( "Screenshot can only be replaced, not deleted." );
		}
		this.screenshot = screenshot;
	}

	public boolean hasContainedElements() {
		return !containedComponents.isEmpty();
	}

	@Override
	public int compareTo( final Element other ) {
		final int result = identifyingAttributes.compareTo( other.getIdentifyingAttributes() );
		if ( result != 0 ) {
			return result;
		}
		return attributes.compareTo( other.getAttributes() );
	}

	@Override
	public boolean equals( final Object obj ) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null || getClass() != obj.getClass() ) {
			return false;
		}
		final Element other = (Element) obj;
		if ( !identifyingAttributes.equals( other.identifyingAttributes ) ) {
			return false;
		}
		if ( !attributes.equals( other.attributes ) ) {
			return false;
		}
		return containedComponents.equals( other.containedComponents );
	}

	@Override
	public int hashCode() {
		if ( hashCodeCache == null ) {
			hashCodeCache = identifyingAttributes.hashCode() + 31 * attributes.hashCode();
		}
		return hashCodeCache;
	}

	@Override
	public String toString() {
		return identifyingAttributes.toString();
	}
}
