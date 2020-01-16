package edu.illinois.cs427.mp4;

import static org.junit.Assert.*;
import org.junit.*;
import junit.framework.TestCase;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class LibraryTest {
	@Test
    public void testLibraryConstructorFromFile1() {
        //TODO implement this
		Library lib = new Library("src/test/resources/constructor.txt");
		assertEquals(0, lib.getCollections().size());
    }

	@Test
    public void testSaveLibraryToFile1() {
        //TODO implement this
		
		Library lib = new Library("src/test/resources/constructor.txt");
        lib.saveLibraryToFile("src/test/resources/save.txt");
		
        String line_cons, line_save = null;
        Boolean same_lib = true;
		
        try {
            FileReader file_cons = new FileReader("src/test/resources/constructor.txt");
			FileReader file_save = new FileReader("src/test/resources/save.txt");
			
            BufferedReader buff_cons = new BufferedReader(file_cons);
            BufferedReader buff_save = new BufferedReader(file_save);
			
            while(true){
				line_cons = buff_cons.readLine();
				line_save = buff_save.readLine();
				
				if (line_cons != null && line_save != null) {
					if(!line_cons.equals(line_save)){
						same_lib = false;
					}
				} else {
					break;
				}  
            }
			
            buff_cons.close();
            buff_save.close();
			
        } catch(IOException e) {
            e.printStackTrace();
        }
		
        assertTrue(same_lib);
    }
	
}
