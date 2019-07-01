/**
 * wfeerg.
 */
public class Tree {
    /**
     * egeg.
     */
    private int value;
    /**
     * ergeger.
     */
    private Tree parent;
    /**
     * hwewerhe.
     */
    private Tree left;
    /**
     * regeg.
     */
    private Tree right;

    /**
     * ergeer.
     *
     * @param setValue grege.
     */
    public Tree(final int setValue) {
        if (this.parent == null) {
            this.value = setValue;
        }
    }

    /**
     * @param setValue  erfefgre.
     * @param setParent erh.
     */
    public Tree(final int setValue, final Tree setParent) {
        this.value = setValue;
        this.parent = setParent;
    }

    /**
     * @param newValue ergerge.
     * @return ergerge.
     */
    public boolean insert(final int newValue) {
        if (newValue > this.value) {
            if (this.right == null) {
                this.right = new Tree(newValue, this);
                return true;
            } else {
                return this.right.insert(newValue);
            }
        } else if (newValue < this.value) {
            if (this.left == null) {
                this.left = new Tree(newValue, this);
                return true;
            } else {
                return this.left.insert(newValue);
            }
        } else {
            return false;
        }
    }

    /**
     * grere.
     *
     * @return rgeger.
     */
    public int minimum() {
        if (this.left != null) {
            return this.left.minimum();
        } else {
            return this.value;
        }
    }

    /**
     * wfwef.
     *
     * @return ergrege.
     */
    public int maximum() {
        if (this.right != null) {
            return this.right.maximum();
        } else {
            return this.value;
        }
    }

    /**
     * gerge.
     *
     * @param searchValue egerg.
     * @return ergreg.
     */
    public Tree search(final int searchValue) {
        if (this.value == searchValue) {
            return this;
        } else if (searchValue > this.value && this.right != null) {
            return this.right.search(searchValue);
        } else if (searchValue < this.value && this.left != null) {
            return this.left.search(searchValue);
        }
        return null;
    }

    /**
     * egreg.
     *
     * @return ergre.
     */
    public int depth() {
        if (this.parent == null) {
            return 0;
        } else {
            return this.parent.depth() + 1;
        }
    }

    /**
     * rtherhe.
     *
     * @return egweh.
     */
    public int descendants() {
        if (this.left == null) {
            if (this.right == null) {
                return 0;
            } else {
                return this.right.descendants() + 1;
            }
        } else {
            if (this.right == null) {
                return this.left.descendants() + 1;
            } else {
                return this.left.descendants() + 1 + this.right.descendants() + 1;
            }
        }
    }
}
