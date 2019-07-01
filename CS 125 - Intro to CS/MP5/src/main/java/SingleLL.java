/**
 * A class that implements a linked list storing integers.
 * <p>
 * You need to complete this class by adding methods to insert, remove, and swap values.
 * Note that our test suites walks this list internally, so you need to maintain the start
 * reference and the structure of the Node class for the tests to pass.
 * <p>
 * Like trees, linked lists are also <i>recursive data structure</i>, since each part of the list is
 * itself a list. However, linked lists can be manipulated easily using both recursive and
 * iterative algorithms. We leave the choice to you.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/5/">MP5 Documentation</a>
 * @see <a href="https://en.wikipedia.org/wiki/Linked_list">Linked List</a>
 */
public class SingleLL {

    /** Start of this linked list. */
    private Node start;

    /**
     * EVEGE.
     */
    SingleLL() { }

    /**
     * Get a reference to the start of the list.
     * <p>
     * <strong>Do not remove or break this method.</strong> The testing suite uses it to do
     * its own testing.
     *
     * @return a reference to the start of the list
     */
    public Node getStart() {
        return start;
    }

    /**
     * Add a new value to the front of the list.
     * <p>
     * <strong>Do not remove or break this method.</strong> The testing suite uses it to do
     * its own testing.
     *
     * @param newValue the new int value to add to the front of the list
     */
    public void unshift(final int newValue) {
        start = new Node(newValue, start);
    }
    /**
     * Empty constructor.
     * <p>
     * <strong>Do not remove or break this method.</strong> The testing suite uses it to do
     * its own testing. You may want to add other constructors as needed.
     */

    /**
     *
     * @param newValue gegeg.
     * @param position erege.
     * @return egreg.
     */
    public boolean insert(final int newValue, final int position) {
        if (position == 0) {
            start = new Node(newValue, start);
            return true;
        }
        if (position < 0) {
            return false;
        }
        Node temp = this.getStart();
        int current = 0;

        while (current < position - 1) {
            if (temp.next == null) {
                return false;
            }
            temp = temp.next;
            current++;
        }
        Node newNode = new Node(newValue, temp.next);
        temp.next = newNode;
        return true;
    }

    /**
     *
     * @param position gege.
     * @return geege.
     */
    public boolean remove(final int position) {
        if (position == 0) {
            this.start = this.getStart().getNext();
            return true;
        }
        if (position < 0) {
            return false;
        }
        Node temp = this.getStart();
        int count = 0;
        while (count < position - 1) {
            if (temp.next == null) {
                return false;
            }
            temp = temp.next;
            count++;
        }
        if (temp.next == null) {
            return false;
        }
        temp.next = temp.next.next;
        return true;
    }

    /**
     *
     * @param firstPosition egregre.
     * @param secondPosition brrt.
     * @return tyjytj.
     */
    public boolean swap(final int firstPosition, final int secondPosition) {
        if (firstPosition < 0 || secondPosition < 0) {
            return false;
        }
        Node temp1 = getStart();
        Node temp2 = getStart();
        int count1 = 0;
        int count2 = 0;
        int tempInt = 0;
        while (count1 < firstPosition) {
            if (temp1.next == null) {
                return false;
            }
            temp1 = temp1.next;
            count1++;
        }
        tempInt = temp1.getValue();
        while (count2 < secondPosition) {
            if (temp2.next == null) {
                return false;
            }
            temp2 = temp2.next;
            count2++;
        }
        temp1.value = temp2.value;
        temp2.value = tempInt;
        return true;
    }

    /**
     * geger.
     *
     * @return fwge.
     */
//    @Override
//    public String toString() {
//        StringBuilder temp = new StringBuilder("[");
//        Node node;
//        if (this.start != null) {
//            node = this.start;
//        } else {
//            return null;
//        }
//        while (node.getNext() != null) {
//            temp.append(node.value);
//            node = node.getNext();
//        }
//        temp.append("]");
//        return temp.toString();
//    }

    /**
     * Internal class storing a node in our SingleLL.
     */
    public final class Node {
        /**
         * geee.
         */
        private int value;
        /** Reference to the next node in the list. */
        private Node next;

        /**
         * Full constructor for the Node class.
         *
         * @param setValue the int value this node will store
         * @param setNext the next node in the list, or null if this node is the end of the list
         */
        Node(final int setValue, final Node setNext) {
            this.setValue(setValue);
            this.setNext(setNext);
        }

        /**
         * Get this node's value.
         *
         * @return this node's value
         */
        public int getValue() {
            return value;
        }

        /**
         * Set this node's value.
         *
         * @param setValue new value for this node
         */
        void setValue(final int setValue) {
            value = setValue;
        }

        /**
         * Get this node's next node.
         *
         * @return this node's next node
         */
        public Node getNext() {
            return next;
        }

        /**
         * Set this node's next node.
         *
         * @param setNext new next node for this node
         */
        void setNext(final Node setNext) {
            next = setNext;
        }
    }
}
