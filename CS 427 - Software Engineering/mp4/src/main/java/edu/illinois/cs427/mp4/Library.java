package edu.illinois.cs427.mp4;

import java.util.List;
import java.io.*;
import java.util.LinkedList;

/**
 * Container class for all the collections (that eventually contain books).
 * Its main responsibility is to save the collections to a file and restore them from a file.
 */
public final class Library {
    private List<Collection> collections;

    /**
     * Builds a new, empty library.
     */
    public Library() {
        // TODO implement this
		this.collections = new LinkedList<Collection>();
    }

    /**
     * Builds a new library and restores its contents from the given file.
     *
     * @param fileName the file from where to restore the library.
     */
    public Library(String fileName) {
        // TODO implement this
		
		this.collections = new LinkedList<Collection>();
		
        try {
            FileReader file = new FileReader(fileName);
            BufferedReader buff = new BufferedReader(file);
			
            while(true){
				String line = buff.readLine();
				if (line != null) {
					this.collections.add(Collection.restoreCollection(line));
				} else {
					break;
				}
            }
			
			buff.close();
        } catch(IOException e) {
			e.printStackTrace();
        }

    }

    /**
     * Saved the contents of the library to the given file.
         *
     * @param fileName the file where to save the library
     */
    public void saveLibraryToFile(String fileName) {
        // TODO implement this
		
		if(collections == null || collections.size() == 0) {
    		return;
		}
		try {
            PrintWriter p = new PrintWriter(fileName);
            for(Collection c : collections){
                p.println(c.getStringRepresentation());
            }
            p.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the collections contained in the library.
     *
     * @return library contained elements
     */
    public List<Collection> getCollections() {
        // TODO implement this
        return this.collections;
    }

    /**
     * Add collections to the library.
     *
     * @param collection to be added to the library
     */
    public void addCollection(Collection collection) {
        // TODO implement this
		this.collections.add(collection);
    }
   
}
