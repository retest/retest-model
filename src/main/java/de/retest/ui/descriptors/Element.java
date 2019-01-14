package de.retest.ui.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

	// TODO Change filter method of IgnoreComponents to make this an Collections.unmodifiableList.
	@XmlElement
	@XmlJavaTypeAdapter( RenderContainedElementsAdapter.class )
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

	public Element( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes ) {
		this( retestId, identifyingAttributes, attributes, new ArrayList<Element>() );
	}

	public Element( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final Element... containedComponents ) {
		this( retestId, identifyingAttributes, attributes, new ArrayList<>( Arrays.asList( containedComponents ) ) );
	}

	public Element( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final List<Element> containedComponents ) {
		this( retestId, identifyingAttributes, attributes, containedComponents, null );
	}

	public Element( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final Screenshot screenshot ) {
		this( retestId, identifyingAttributes, attributes, new ArrayList<Element>(), screenshot );
	}

	public Element( final String retestId, final IdentifyingAttributes identifyingAttributes,
			final Attributes attributes, final List<Element> containedComponents, final Screenshot screenshot ) {
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
		this.containedComponents = containedComponents;
		this.screenshot = screenshot;
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
			hashCodeCache = Objects.hash( identifyingAttributes, attributes, containedComponents );
		}
		return hashCodeCache;
	}

	@Override
	public String toString() {
		return identifyingAttributes.toString();
	}

	public IdentifyingAttributes getIdentifyingAttributes() {
		return identifyingAttributes;
	}

	public List<Element> getContainedElements() {
		return containedComponents;
	}

	public boolean hasContainedElements() {
		return !containedComponents.isEmpty();
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

	public Attributes getAttributes() {
		return attributes;
	}

	public Element applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}

		final IdentifyingAttributes newIdentAttributes = identifyingAttributes
				.applyChanges( actionChangeSet.getIdentAttributeChanges().getAll( identifyingAttributes ) );

		final Attributes newAttributes =
				attributes.applyChanges( actionChangeSet.getAttributesChanges().getAll( identifyingAttributes ) );
		final List<Element> newContainedComps = createNewComponentList( actionChangeSet, newIdentAttributes );

		return new Element( retestId, newIdentAttributes, newAttributes, newContainedComps, screenshot );
	}

	protected List<Element> createNewComponentList( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes ) {
		List<Element> newContainedComps = containedComponents;
		newContainedComps = removeDeleted( actionChangeSet, newContainedComps );
		newContainedComps = applyChangesToContainedComponents( actionChangeSet, newIdentAttributes, newContainedComps );
		newContainedComps = addInserted( actionChangeSet, newIdentAttributes, newContainedComps );
		return newContainedComps;
	}

	private List<Element> removeDeleted( final ActionChangeSet actionChangeSet,
			final List<Element> oldContainedComps ) {
		final Set<IdentifyingAttributes> deletedChanges = actionChangeSet.getDeletedChanges();
		final List<Element> newContainedComps = new ArrayList<>( oldContainedComps.size() );

		for ( final Element oldComp : oldContainedComps ) {
			if ( !deletedChanges.contains( oldComp.getIdentifyingAttributes() ) ) {
				newContainedComps.add( oldComp );
			}
		}

		return newContainedComps;
	}

	private List<Element> applyChangesToContainedComponents( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> oldContainedComps ) {
		final List<Element> newContainedComps = new ArrayList<>( oldContainedComps.size() );

		for ( final Element oldComp : oldContainedComps ) {
			addPathChangeToChangeSet( actionChangeSet, newIdentAttributes, oldComp );
			newContainedComps.add( oldComp.applyChanges( actionChangeSet ) );
		}

		return newContainedComps;
	}

	private void addPathChangeToChangeSet( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final Element oldComp ) {
		if ( ObjectUtils.notEqual( identifyingAttributes.getPathTyped(), newIdentAttributes.getPathTyped() ) ) {
			final Path oldPath = oldComp.identifyingAttributes.getPathTyped();
			final Path newPath = Path.fromString( newIdentAttributes.getPath() + Path.PATH_SEPARATOR
					+ oldComp.identifyingAttributes.getPathElement().toString() );

			actionChangeSet.getIdentAttributeChanges().add( oldComp.identifyingAttributes,
					new AttributeDifference( "path", oldPath, newPath ) );
		}
	}

	private List<Element> addInserted( final ActionChangeSet actionChangeSet,
			final IdentifyingAttributes newIdentAttributes, final List<Element> newContainedComps ) {
		for ( final Element insertedComp : actionChangeSet.getInsertedChanges() ) {
			if ( isParent( newIdentAttributes, insertedComp.identifyingAttributes ) ) {
				newContainedComps.add( insertedComp );
			}
		}
		return newContainedComps;
	}

	private static boolean isParent( final IdentifyingAttributes parentIdentAttributes,
			final IdentifyingAttributes containedIdentAttributes ) {
		return parentIdentAttributes.getPathTyped().equals( containedIdentAttributes.getParentPathTyped() );
	}

	public int countAllContainedElements() {
		// count current component!
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

	public String getRetestId() {
		return retestId;
	}
}
