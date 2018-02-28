package util.queue;

import java.util.*;

public class FibonacciHeap<T> implements ExtendedPriorityQueue<T> {

    private final Map<Long, Node<T>> idToNode;
    private final Comparator<? super T> comparator;

    private Node<T> min = null;
    private int size = 0;
    private long nextId = 0;

    public FibonacciHeap(final Comparator<? super T> c) {
        comparator = c;
        idToNode = new HashMap<>();
    }

    public static <S extends Comparable<? super S>> FibonacciHeap<S> naturallyOrdered() {
        return new FibonacciHeap<>(S::compareTo);
    }

    private void insertAfter(Node<T> node, Node<T> newNode) {
        // insert newNode after node
        newNode.next = node.next;
        newNode.prev = node;
        node.next.prev = newNode;
        node.next = newNode;
    }

    private void insertIntoRootList(Node<T> node) {
        node.parent = null;
        if (min == null) {
            node.next = node;
            node.prev = node;
            min = node;
        } else {
            insertAfter(min, node);
            if (comparator.compare(min.element, node.element) > 0)
                min = node;
        }
    }

    private void unlink(Node<T> node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    // merge heaps of equal size (see cormen et al)
    private void consolidate() {
        // number of components is bounded by log[phi](size)
        int max = (int) Math.ceil(Math.log(size) / Math.log(1.5));
        ArrayList<Node<T>> heapOfDegree = new ArrayList<>();
        for (int i = 0; i < max; i++)
            heapOfDegree.add(null);
        // for each node in the root list
        Node<T> start = min;
        // remember starting point
        Node<T> current = start;
        do {
            // current.next will be changed, so we store it
            Node<T> next = current.next;

            // grab node and degree
            Node<T> x = current;
            int d = x.degree;
            // remove any links from x
            x.next = x;
            x.prev = x;
            while (heapOfDegree.get(d) != null) {
                // node of same rank
                Node<T> y = heapOfDegree.get(d);
                // let y have greater key
                if (comparator.compare(x.element, y.element) > 0) {
                    Node<T> temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                heapOfDegree.set(d, null);
                d = d + 1;
            }
            // remove x from root list
            heapOfDegree.set(d, x);

            // advance to next node
            current = next;
        } while (current != start);

        // reset min
        min = null;

        for (Node<T> heap : heapOfDegree) {
            if (heap == null)
                continue;
            insertIntoRootList(heap);
        }
    }

    // move y from root list to the child list of x (see cormen et al)
    private void link(Node<T> y, Node<T> x) {
        // remove y from root list
        unlink(y);
        // make y a child of x
        y.parent = x;
        if (x.child == null) {
            x.child = y;
            y.next = y;
            y.prev = y;
        } else {
            insertAfter(x.child, y);
        }
        // increment degree
        x.degree++;
        // unmark y
        y.marked = false;
    }

    // cut x from y's children and add it to the root list (see cormen et al)
    private void cut(Node<T> x, Node<T> y) {
        // remove x from child list of y
        x.next.prev = x.prev;
        x.prev.next = x.next;
        if (x == x.next) {
            y.child = null;
        } else {
            y.child = x.next;
        }
        // decrement degree of y
        y.degree--;
        // add x to root list
        insertIntoRootList(x);
        // unmark x
        x.marked = false;
    }

    // continue cutting marked nodes (see cormen et al)
    private void cascadingCut(Node<T> node) {
        Node<T> parent = node.parent;
        if (parent == null)
            return;
        if (!node.marked) {
            node.marked = true;
        } else {
            cut(node, parent);
            cascadingCut(parent);
        }
    }

    @Override
    public T peek() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        return min.element;
    }

    @Override
    public void insert(T element) {
        insertIntoRootList(new Node<>(element, -1));
        size++;
    }

    @Override
    public long insertWithId(T element) {
        final long id = nextId++;
        Node<T> node = new Node<>(element, id);
        insertIntoRootList(node);
        idToNode.put(id, node);
        size++;
        return id;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T pop() {
        if (size() == 0)
            throw new NoSuchElementException("Heap is empty.");
        Node<T> z = min;
        // for each child of z
        Node<T> start = z.child;
        if (start != null) {
            // remember starting point
            Node<T> current = start;
            do {
                // current.next will be overridden in insertIntoRootList
                Node<T> next = current.next;
                // add child to root list
                insertIntoRootList(current);
                // advance
                current = next;
            } while (current != start);
        }
        // remove z from root list
        unlink(z);
        if (z == z.next) {
            min = null;
        } else {
            min = z.next;
            consolidate();
        }
        size--;
        return z.element;
    }

    @Override
    public void decrease(long id) {
        if (!idToNode.containsKey(id))
            throw new NoSuchElementException("Invalid identifier.");
        Node<T> node = idToNode.get(id);
        Node<T> parent = node.parent;
        // check if heap property was violated
        if (parent != null && comparator.compare(node.element, parent.element) < 0) {
            cut(node, parent);
            cascadingCut(parent);
        }
        // adjust min
        if (comparator.compare(min.element, node.element) > 0)
            min = node;
    }

    private void appendList(final Node<T> start, final StringBuilder sb, final int level) {
        if (start == null)
            return;

        Node<T> current = start;
        do {
            for (int i = 0; i < level - 1; i++)
                sb.append("  ");
            if (level > 0)
                sb.append("\u2514 ");
            sb.append(current.extendedString()).append('\n');
            appendList(current.child, sb, level + 1);
            current = current.next;
        } while (current != start);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendList(min, sb, 0);
        return sb.toString();
    }

    private static final class Node<T> {
        final T element;
        final long id;

        int degree = 0;
        boolean marked = false;

        Node<T> parent = null;
        Node<T> child = null;

        Node<T> next = null;
        Node<T> prev = null;

        Node(T element, long id) {
            this.element = element;
            this.id = id;
        }

        @Override
        public String toString() {
            return String.format("Node%s(%s)", marked ? "*" : "", element);
        }

        String extendedString() {
            return String.format("Node%s(%s, prev=%s, next=%s, parent=%s, child=%s)",
                    marked ? "*" : "", element, prev, next, parent, child);
        }
    }
}
