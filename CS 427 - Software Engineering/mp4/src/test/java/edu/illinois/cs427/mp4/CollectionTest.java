package edu.illinois.cs427.mp4;

import static org.junit.Assert.*;
import org.junit.*;
import junit.framework.TestCase;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class CollectionTest {
	@Test
    public void testRestoreCollection1() {
        //TODO implement this
		// Collection test = new Collection("TestCollection");
        // Book testBook = new Book("TestBook", "hhhh");
        // test.addElement(testBook);
        // List<Element> elementsInCollection = test.getElements();
        // assertTrue(elementsInCollection.contains(testBook));
		Collection c = Collection.restoreCollection("[test,<sb241-angrave#[subset,<sb374-jeff#>]#>]");
        assertEquals(2, c.getElements().size());
		Collection subset = (Collection) c.getElements().get(1);
        assertEquals("sb241", ((Book) c.getElements().get(0)).getTitle());
        assertEquals("subset", ((Collection) c.getElements().get(1)).getName());
        assertEquals("jeff", ( ((Book) subset.getElements().get(0)).getAuthor()));
    }
	
	@Test
    public void testGetStringRepresentation1() {
        Collection c = new Collection("test");
        Book test374 = new Book("sb374", "jeff");
        Book test241 = new Book("sb241", "angrave");
        c.addElement(test374);
        c.addElement(test241);
        assertEquals("[test,<sb374-jeff#sb241-angrave#>]", c.getStringRepresentation());
    }
	
	@Test
    public void testAddElement1() {
        //TODO implement this
		Collection c = new Collection("test");
        Book test374 = new Book("sb374", "jeff");
        c.addElement(test374);
        assertEquals(1, c.getElements().size());
    }
	
	@Test
	public void testAddElementFail() {
        Collection c = new Collection("test");
        Book test374 = new Book("sb374", "jeff");
        c.addElement(test374);
        Collection collection2 = new Collection("testCollection2");
        assertFalse(collection2.addElement(test374));
    }
	
	@Test
    public void testDeleteElement1() {
        //TODO implement this
		Collection c = new Collection("test");
        Book test374 = new Book("sb374", "jeff");
        c.addElement(test374);
        assertEquals(1, c.getElements().size());
        c.deleteElement(test374);
        assertEquals(0, c.getElements().size());
    }
	
	@Test
	public void testDeleteElementFail() {
        Collection c = new Collection("test");
        Book test374 = new Book("sb374", "jeff");
        assertFalse(c.deleteElement(test374));
    }
}
