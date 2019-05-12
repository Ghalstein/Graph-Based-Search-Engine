/******************************************************************************
 *  Compilation:  javac LinkedList.java
 *  Execution:    java LinkedList 
 ******************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;
import java.awt.Color;

public class LinkedList<Item> implements Iterable<Item> { // to enable for each syntax
    private int n;          // number of elements
    private Node first;     // beginning of the list
    private Node last;      // end of the list

    // helper linked list class
    private class Node {
        private Item item;
        private Node next;
    }

    /** Initializes an empty list. */
    public LinkedList() {
        first = null;
	last = null;
        n = 0;
    }

    public int getN(){
        return n;
    }

    public boolean isEmpty() { return n == 0; }

    public int size() { return n; }

    /* add to beginning of list */
    public void addFirst(Item item) { 
        Node oldfirst = first;
        first = new Node();
	if (last == null)
	    last = first;
        first.item = item;
        first.next = oldfirst;
        n++;
    }

    /* add to end of list */
    public void addLast(Item item) {
        Node temp = new Node();
	temp.item = item;
	if (last == null) {
	    first = temp;
	    last = temp;
	}
	else {
	    last.next = temp;
	    last = temp;
	}
        n++;
    }
    
    /** Removes the item at the beginning of the list. **/
    public Item removeFirst() {
        if (isEmpty())
	    throw new NoSuchElementException("List empty");
        Item item = first.item;        // save item to return
        first = first.next;            // delete first node
        n--;
        if (isEmpty()) last = null;    // to avoid loitering
        return item;                   // return the saved item
    }

    public void remove(Item v) {
	Node ptr = first;
	Node prev = null;
	while (ptr != null && ! ptr.item.equals(v)) {
	    prev = ptr;
	    ptr = ptr.next;
	}
	if (ptr == first)
	    first = first.next;
	else
	    prev.next = ptr.next;
    }

    /** Returns (but does not remove) the first element in the list. */
    public Item getFirst() {
        if (isEmpty())
	    throw new NoSuchElementException("List empty");
        return first.item;
    }

    // Suppose Item extends Comparable<Item>. We can search for
    // an item as follows.
    
    public boolean exists(Item v) {
    	Node ptr = first;
    	while (ptr != null) {
    	    if (ptr.item.equals(v))
    		return true;
    	    ptr = ptr.next;
    	}
    	return false;
    }

    /**
     * Returns an iterator to this list that traverses the items from first to last.
     */
    public Iterator<Item> iterator() {
	return new ListIterator();
    }

    // The Iterator<T> interface contains methods hasNext(), remove(), next()
    // (remove() is optional)
    private class ListIterator implements Iterator<Item> {
        private Node current = first;
        public boolean hasNext()  {
	    return current != null;
	}
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this)
            s.append(item + " ");
        return s.toString() + "**";
    }     

    /** Unit test with some pictures */

    public static void main(String[] args) {

	// This is possible if LinkedList implements Iterable
	// for (Picture p:photoStack) {
	//     p.show();
	//     pause(2000);
	// }
    }
}