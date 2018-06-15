package de.retest.ui.descriptors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.retest.ui.image.Screenshot;
import de.retest.ui.review.ActionChangeSet;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class RootElement extends Element {

	private static final long serialVersionUID = 2L;

	@XmlAttribute
	private final int screenId;
	@XmlAttribute
	private final String screen;
	@XmlAttribute
	private final String title;

	@SuppressWarnings( "unused" )
	private RootElement() {
		// for JAXB
		screenId = 0;
		screen = null;
		title = null;
	}

	public RootElement( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot, final List<Element> containedComponents, final String screen,
			final int screenId, final String title ) {
		super( identifyingAttributes, attributes, containedComponents );
		setScreenshot( screenshot );
		this.screen = screen;
		this.screenId = screenId;
		this.title = title;
	}

	public String getScreen() {
		return screen;
	}

	public int getScreenId() {
		return screenId;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public RootElement applyChanges( final ActionChangeSet actionChangeSet ) {
		if ( actionChangeSet == null ) {
			return this;
		}

		final IdentifyingAttributes newIdentAttributes;
		newIdentAttributes = identifyingAttributes
				.applyChanges( actionChangeSet.getIdentAttributeChanges().getAll( identifyingAttributes ) );

		final Attributes newAttributes =
				attributes.applyChanges( actionChangeSet.getAttributesChanges().getAll( identifyingAttributes ) );

		final List<Element> newContainedComponents = createNewComponentList( actionChangeSet, newIdentAttributes );

		return new RootElement( newIdentAttributes, newAttributes, screenshot, newContainedComponents, screen, screenId,
				title );
	}

	public static List<Screenshot> getScreenshots( final List<RootElement> windows ) {
		final List<Screenshot> result = new ArrayList<Screenshot>();
		for ( final RootElement rootElement : windows ) {
			result.add( rootElement.getScreenshot() );
		}
		return result;
	}
}
