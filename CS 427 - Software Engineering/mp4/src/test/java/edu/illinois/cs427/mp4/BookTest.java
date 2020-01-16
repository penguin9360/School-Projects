package edu.illinois.cs427.mp4;

import static org.junit.Assert.*;
import org.junit.*;
import junit.framework.TestCase;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class BookTest {
	
	@Test
    public void testBookConstructor1() {
        //TODO implement this
		Book book = new Book("sb374", "jeff");
        assertEquals("sb374", book.getTitle());
        assertEquals("jeff", book.getAuthor());
    }
	
	@Test
    public void testBookConstructor2() {
        //TODO implement this
		Book book = new Book("sb241", "angrave");
        assertEquals("sb241", book.getTitle());
        assertEquals("angrave", book.getAuthor());
    }
	
	@Test
    public void testGetStringRepresentation1() {
        //TODO implement this
		Book book = new Book("cs374", "jeff");
        assertEquals("cs374-jeff", book.getStringRepresentation());
    }
	
	@Test
    public void testGetContainingCollections1() {
        //TODO implement this
		Collection sb = new Collection("test");
		
        Book sb374 = new Book("sb374", "jeff");
        Book sb241 = new Book("sb241", "angrave");
		
        Collection another_sb = new Collection("test_subset");
		
        another_sb.addElement(sb374);
		
        sb.addElement(sb241);
        sb.addElement(another_sb);
		
        List<Collection> collection_374 = sb374.getContainingCollections();
        assertEquals(2, collection_374.size());
        assertEquals("test_subset", collection_374.get(0).getName());
        assertEquals("test", collection_374.get(1).getName());
    }
}
