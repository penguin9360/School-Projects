package edu.illinois.cs427.mp4;

import java.util.List;
import java.util.LinkedList;

/**
 * Represents a collection of books or (sub)collections.
 */
public final class Collection extends Element {
    List<Element> elements;
    private String name;
    
    /**
     * Creates a new collection with the given name.
     *
     * @param name the name of the collection
     */
    public Collection(String name) {
        // TODO implement this
		this.name = name;
		this.elements = new LinkedList<Element> ();
    }

    /**
     * Restores a collection from its given string representation.
     *
     * @param stringRepresentation the string representation
     */
    public static Collection restoreCollection(String stringRepresentation) {
        // TODO implement this
        String str_rep = stringRepresentation.substring(1, stringRepresentation.length() - 1);
        Collection collection = new Collection(str_rep.split(",")[0]);
		
        int idx = str_rep.indexOf(',') + 2;
        str_rep = str_rep.substring(idx, str_rep.length() - 1);
		
        while(str_rep.length() != 0) {
            if (str_rep.charAt(0) == '['){
                String tmp = str_rep.split("]#", 2)[0];
                Collection tmp2 = Collection.restoreCollection(tmp + "]");
                collection.addElement(tmp2);
                str_rep = str_rep.split("]#", 2)[1];
            }else{
                String tmp = str_rep.split("#", 2)[0];
                Book tmp2 = new Book(tmp);
                collection.addElement(tmp2);
                str_rep = str_rep.split("#", 2)[1];
            }
        }
        return collection;
    }

    /**
     * Returns the string representation of a collection. 
     * The string representation should have the name of this collection, 
     * and all elements (books/subcollections) contained in this collection.
     *
     * @return string representation of this collection
     */
    public String getStringRepresentation() {
        // TODO implement this
        String str_rep ="[" + this.name + ",<";
        for(int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getClass().getName() == "edu.illinois.cs427.mp4.Book") {
                str_rep += ((Book) elements.get(i)).getStringRepresentation() + "#";
            } else {
                str_rep += ((Collection) elements.get(i)).getStringRepresentation() + "#";
            }
        }
        str_rep += ">]";
        return str_rep;
    }

    /**
     * Returns the name of the collection.
     *
     * @return the name
     */
    public String getName() {
        // TODO implement this
        return this.name;
    }
    
    /**
     * Adds an element to the collection.
     * If parentCollection of given element is not null,
     * do not actually add, but just return false.
     * (explanation: if given element is already a part of  
     * another collection, you should have deleted the element 
     * from that collection before adding to another collection)
     *
     * @param element the element to add
     * @return true on success, false on fail
     */
    public boolean addElement(Element element) {
        // TODO implement this
        if(element.getParentCollection() == null){
            elements.add(element);
            element.setParentCollection(this);
            return true;
        }
        return false;
    }
    
    /**
     * Deletes an element from the collection.
     * Returns false if the collection does not have 
     * this element.
     * Hint: Do not forget to clear parentCollection
     * of given element 
     *
     * @param element the element to remove
     * @return true on success, false on fail
     */
    public boolean deleteElement(Element element) {
        // TODO implement this
		if (this.elements.contains(element) == false) {
			return false;
		}
        this.elements.remove(element);
        element.setParentCollection(null);
        return true;
    }
    
    /**
     * Returns the list of elements.
     * 
     * @return the list of elements
     */
    public List<Element> getElements() {
        // TODO implement this
		
        return this.elements;
    }
}
