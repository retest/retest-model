package de.retest.ui.descriptors;

import static de.retest.ui.Path.fromString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import de.retest.ui.Path;
import de.retest.ui.review.ActionChangeSet;

public class ElementTest {

	private static class Parent {}

	@Test
	public void toString_returns_UniqueCompIdentAttributes_toString() throws Exception {
		final IdentifyingAttributes compIdentAttributes =
				IdentifyingAttributes.create( fromString( "Window/path/Component" ), java.awt.Component.class );
		assertThat( new Element( "asdef", compIdentAttributes, null ).toString() )
				.isEqualTo( compIdentAttributes.toString() );
		assertThat( compIdentAttributes.toString() ).isEqualTo( "Component" );
	}

	@Test
	public void applyChanges_to_path_propagates_to_child_components() {
		// Window
		//   |- Parent_0        = root
		//     |- Parent_0      = parent0
		//       |- Component_0 = comp0
		//     |- Component_1   = comp1
		//     |- Component_2   = comp2
		final Element comp0 = createElement( "Window/Parent_0/Parent_0/Component_0", java.awt.Component.class );
		final Element parent0 = createElement( "Window/Parent_0/Parent_0", java.awt.Component.class, comp0 );
		final Element comp1 = createElement( "Window/Parent_0/Component_1", java.awt.Component.class );
		final Element comp2 = createElement( "Window/Parent_0/Component_2", java.awt.Component.class );
		final Element root = createElement( "Window/Parent_0", Parent.class, parent0, comp1, comp2 );

		final ActionChangeSet changes = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		changes.getIdentAttributeChanges().add( root.getIdentifyingAttributes(), new AttributeDifference( "path",
				root.getIdentifyingAttributes().getPathTyped(), Path.fromString( "Window/Parent_1" ) ) );

		final Element newRoot = root.applyChanges( changes );
		final Element newComp2 = newRoot.getContainedElements().get( 2 );
		final Element newComp1 = newRoot.getContainedElements().get( 1 );
		final Element newParent0 = newRoot.getContainedElements().get( 0 );
		final Element newComp0 = newParent0.getContainedElements().get( 0 );

		assertThat( newRoot.getIdentifyingAttributes().getPath() ).isEqualTo( "Window/Parent_1" );
		assertThat( newComp2.getIdentifyingAttributes().getPath() ).isEqualTo( "Window/Parent_1/Component_2" );
		assertThat( newComp1.getIdentifyingAttributes().getPath() ).isEqualTo( "Window/Parent_1/Component_1" );
		assertThat( newParent0.getIdentifyingAttributes().getPath() ).isEqualTo( "Window/Parent_1/Parent_0" );
		assertThat( newComp0.getIdentifyingAttributes().getPath() ).isEqualTo( "Window/Parent_1/Parent_0/Component_0" );
	}

	@Test
	public void applyChanges_should_add_inserted_components() throws Exception {
		final Element parent = createElement( "Parent_0", java.awt.Component.class );
		final Element newChild = createElement( "Parent_0/NewChild_0", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( newChild );
	}

	@Test
	public void applyChanges_should_remove_deleted_components() throws Exception {
		final Element oldChild = createElement( "Parent_0/NewChild_0", java.awt.Component.class );
		final Element parent = createElement( "Parent_0", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( oldChild.getIdentifyingAttributes() );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void parent_update_should_not_affect_insertion() {
		final Element parent = createElement( "ParentPathOld_0", java.awt.Component.class );
		final Element newChild = createElement( "ParentPathNew_0/NewChild_0", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.getIdentAttributeChanges().add( parent.getIdentifyingAttributes(),
				new AttributeDifference( "path", fromString( "ParentPathOld_0" ), fromString( "ParentPathNew_0" ) ) );
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		assertThat( changed.identifyingAttributes.getPathTyped() ).isEqualTo( fromString( "ParentPathNew_0" ) );
		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( newChild );
	}

	@Test
	public void no_insertion_match_should_not_change_anything() {
		final Element parent = createElement( "ParentPath_0", java.awt.Component.class );
		final Element newChild = createElement( "NotParentPath_0/NewChild_0", java.awt.Component.class );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( newChild );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void applyChanges_should_work_in_complex_scenario() {
		final Element leaf0 = createElement( "Root_0/Branch_0/Leaf_0", java.awt.Component.class );
		final Element leaf1 = createElement( "Root_0/Branch_0/Leaf_1", java.awt.Component.class );
		final Element branch0 = createElement( "Root_0/Branch_0", java.awt.Component.class, leaf0, leaf1 );
		final Element leaf2 = createElement( "Root_0/Branch_1/Leaf_2", java.awt.Component.class );
		final Element newLeaf3 = createElement( "Root_0/Branch_1/NewLeaf_3", java.awt.Component.class );
		final Element branch1 = createElement( "Root_0/Branch_1", java.awt.Component.class, leaf2 );
		final Element root = createElement( "Root_0", java.awt.Component.class, branch0, branch1 );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( leaf1.getIdentifyingAttributes() );
		actionChangeSet.addInsertChange( newLeaf3 );

		final Element changed = root.applyChanges( actionChangeSet );

		final List<Element> branches = changed.getContainedElements();
		assertThat( branches ).hasSize( 2 );
		assertThat( branches.get( 0 ).getContainedElements() ).containsExactly( leaf0 );
		assertThat( branches.get( 1 ).getContainedElements() ).containsExactly( leaf2, newLeaf3 );
	}

	@Test
	public void applyChanges_should_add_intermediate_elements() {
		// window
		final Element window = createElement( "window", java.awt.Component.class );

		// window/path_1/comp_1
		final Element element = createElement( "window/path_0/comp_1", java.awt.Component.class );
		final Element path = createElement( "window/path_0", java.awt.Component.class, element );

		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addInsertChange( path );
		actionChangeSet.addInsertChange( element );

		final Element changed = window.applyChanges( actionChangeSet );

		final List<Element> containedElements = changed.getContainedElements();
		assertThat( containedElements ).containsExactly( path );
		assertThat( containedElements.get( 0 ).getContainedElements() ).containsExactly( element );
	}

	@Test
	public void parent_update_should_not_affect_deletion() {
		final Path parentPathOld = Path.fromString( "ParentPathOld_0" );
		final Path parentPathNew = Path.fromString( "ParentPathNew_0" );
		final Element oldChild = createElement( "ParentPathOld_0/NewChild_0", java.awt.Component.class );
		final Element parent = createElement( "ParentPathOld_0", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.getIdentAttributeChanges().add( parent.getIdentifyingAttributes(),
				new AttributeDifference( "path", parentPathOld, parentPathNew ) );
		actionChangeSet.addDeletedChange( oldChild.getIdentifyingAttributes() );

		final Element changed = parent.applyChanges( actionChangeSet );

		assertThat( changed.identifyingAttributes.getPathTyped() ).isEqualTo( parentPathNew );
		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 0 );
	}

	@Test
	public void no_deletion_match_should_not_change_anything() {
		final Element oldChild = createElement( "ParentPath_0/NewChild_0", java.awt.Component.class );
		final Element parent = createElement( "ParentPath_0", java.awt.Component.class, oldChild );
		final ActionChangeSet actionChangeSet = ActionChangeSetTestUtils.createEmptyActionChangeSet();
		actionChangeSet.addDeletedChange( IdentifyingAttributes.create( Path.fromString( "NotParentPath_0/NewChild_0" ),
				java.awt.Component.class ) );

		final Element changed = parent.applyChanges( actionChangeSet );

		final List<Element> containedComponents = changed.getContainedElements();
		assertThat( containedComponents ).hasSize( 1 );
		assertThat( containedComponents ).contains( oldChild );
	}

	@Test( expected = NullPointerException.class )
	public void null_id_should_throw_exception() {
		new Element( null, IdentifyingAttributes.create( Path.fromString( "NotParentPath_0/NewChild_0" ),
				java.awt.Component.class ), new MutableAttributes().immutable() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void empty_id_should_throw_exception() {
		new Element( "", IdentifyingAttributes.create( Path.fromString( "NotParentPath_0/NewChild_0" ),
				java.awt.Component.class ), new MutableAttributes().immutable() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void whitespace_in_id_should_throw_exception() {
		new Element( " ", IdentifyingAttributes.create( Path.fromString( "NotParentPath_0/NewChild_0" ),
				java.awt.Component.class ), new MutableAttributes().immutable() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void special_chars_in_id_should_throw_exception() {
		new Element( "+(invalid]ID", IdentifyingAttributes.create( Path.fromString( "NotParentPath_0/NewChild_0" ),
				java.awt.Component.class ), new MutableAttributes().immutable() );
	}

	// Copy & paste from ElementBuilder due to cyclic dependency.
	private static Element createElement( final String path, final Class<?> type,
			final Element... containedComponents ) {
		return new Element( "asdas", IdentifyingAttributes.create( fromString( path ), type ), new Attributes(),
				containedComponents );
	}

}
