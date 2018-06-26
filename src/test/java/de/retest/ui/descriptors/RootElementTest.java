package de.retest.ui.descriptors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.image.Screenshot;
import de.retest.ui.image.Screenshot.ImageType;
import de.retest.ui.review.ActionChangeSet;

public class RootElementTest {

	private static class Window {}

	private static class Comp {}

	private final IdentifyingAttributes windowIdentAttributes = RootIdentifyingAttributes
			.create( Path.fromString( "Window" ), Window.class, "name", "Window Title A", "code-loc A" );
	private final IdentifyingAttributes compIdentAttributes = RootIdentifyingAttributes
			.create( Path.fromString( "Window/Comp_0" ), Comp.class, "name", "comp 0", "code-loc A" );
	private final IdentifyingAttributes childIdentAttributes0 = RootIdentifyingAttributes
			.create( Path.fromString( "Window/Comp_0/Comp0" ), Comp.class, "name", "child 0", "code-loc A" );

	private final Screenshot screenshot = new Screenshot( "", new byte[0], ImageType.PNG );

	@Test
	public void applyChanges_adds_inserted_components() throws Exception {
		final Element element = new Element( compIdentAttributes, new Attributes(),
				Collections.singletonList( new Element( childIdentAttributes0, new Attributes() ) ) );
		final RootElement rootElement = descriptorFor( windowIdentAttributes, new Attributes(), screenshot );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( element );

		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( element );
	}

	@Test
	public void applyChanges_removes_deleted_components() throws Exception {
		final Element element = new Element( compIdentAttributes, new Attributes(),
				Collections.singletonList( new Element( childIdentAttributes0, new Attributes() ) ) );
		final RootElement rootElement = descriptorFor( windowIdentAttributes, new Attributes(), screenshot, element );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( element.getIdentifyingAttributes() );

		final RootElement changed = rootElement.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	private RootElement descriptorFor( final IdentifyingAttributes identifyingAttributes, final Attributes attributes,
			final Screenshot screenshot, final Element... childrenArray ) {
		List<Element> children = new ArrayList<Element>();
		if ( childrenArray != null ) {
			children = Arrays.asList( childrenArray );
		}
		return new RootElement( identifyingAttributes, attributes, screenshot, children,
				(String) identifyingAttributes.get( "name" ), identifyingAttributes.get( "name" ).hashCode(),
				identifyingAttributes.get( "text" ) + "-Window" );
	}

	private static class RootIdentifyingAttributes extends IdentifyingAttributes {

		private static final long serialVersionUID = 1L;

		public static IdentifyingAttributes create( final Path path, final Class<?> type, final String name,
				final String text, final String codeLoc ) {
			final Collection<Attribute> parent = IdentifyingAttributes.createList( path, type.getName() );
			parent.add( new StringAttribute( "name", name ) );
			return new RootIdentifyingAttributes( parent );
		}

		@SuppressWarnings( "unused" )
		public RootIdentifyingAttributes() {}

		public RootIdentifyingAttributes( final Collection<Attribute> attributes ) {
			super( attributes );
		}

	}

}
