package util.queue;

import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Collections;

public class SoftHeap<T> implements LossyPriorityQueue<T>, Meldable<SoftHeap<T>> {

    private final double errorRate;
    private final int nodeTargetSize;
    private BinaryHeap queue;
    private Set<T> corruptedElements;
    private final Comparator<? super T> comparator;
    private int size;
    private int rank;

    
    public SoftHeap(double errorRate, Comparator<? super T> comparator, T element) {
        this(errorRate, comparator);
        queue.root.insert(element);
        size = 1;
        rank = 1;
        corruptedElements = new HashSet<>();
    }

    public SoftHeap(double errorRate, Comparator<? super T> comparator) {
        this.errorRate = errorRate;
        // Cause Java doesn't support computing the logarithm to an arbitrary base...
        nodeTargetSize = (int)Math.ceil(Math.log(1 / errorRate) / Math.log(2)) + 5;
        this.comparator = comparator;
        queue = new BinaryHeap();

    }

    public static <S extends Comparable<? super S>> SoftHeap<S> naturallyOrdered(double errorRate) {
        return new SoftHeap<>(errorRate, S::compareTo);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterable<T> corrupted() {
        return corruptedElements;
    }

    @Override
    public T peek() {
        List<? extends T> elements = queue.sufMin.root.nodeElements;
        return elements.get(elements.size() - 1);
    }

    @Override
    public T pop() {
        BinaryHeap minHeap = queue.sufMin;
        List<T> elements = minHeap.root.nodeElements;
        T element = elements.get(elements.size() - 1);
        elements.remove(elements.size() - 1);
        // This binary heap does not contain any elements at all
        if (minHeap.root.sift() == 0) {
            if (minHeap.nextHeap != null) {
                minHeap.nextHeap.previousHeap = minHeap.previousHeap;
            }
            if (minHeap.previousHeap == null) {
                minHeap = minHeap.nextHeap;
            } else {
                minHeap.previousHeap = minHeap.nextHeap;
            }
        }
        corruptedElements.remove(elements);
        size--;
        return element;
    }

    private SoftHeap<T> makeHeap(T element) {
        SoftHeap<T> newHeap = new SoftHeap<T>(errorRate, comparator, element);
        return newHeap;
    }

    @Override
    public void insert(T element) {
        SoftHeap<T> newHeap = makeHeap(element);
        meld(newHeap);
    }

    private BinaryHeap combine(BinaryHeap first, BinaryHeap second) {
        BinaryHeapNode newRoot = new BinaryHeapNode(first.root, second.root, null, first.root.rank);
        BinaryHeap newHeap = new BinaryHeap(newRoot);
        newHeap.previousHeap = first.previousHeap;
        newHeap.nextHeap = second.nextHeap;
        first.nextHeap = null;
        second.previousHeap = null;
        return newHeap;
    }

    @Override
    public SoftHeap<T> meld(SoftHeap<T> other) {
        BinaryHeap thisHeap = this.queue, otherHeap = other.queue;
        BinaryHeap currentQueue;
        int maxRank = 1; 
        // First we merge both Lists of binary heaps and keep them
        // in non-decreasing order of ranks
        if(otherHeap.root.rank < thisHeap.root.rank) {
            currentQueue = otherHeap;
            otherHeap = otherHeap.nextHeap;  
        } else {
            currentQueue = thisHeap;
            thisHeap = thisHeap.nextHeap;
        }
        // Keeps track of the root of the linked list of binary heaps
        BinaryHeap root = currentQueue;
        while (thisHeap != null && otherHeap != null) {
            if (otherHeap.root.rank < thisHeap.root.rank) {
                currentQueue.nextHeap = otherHeap;
                otherHeap.previousHeap = currentQueue;
                maxRank = Math.max(maxRank, otherHeap.root.rank);
                otherHeap = otherHeap.nextHeap;
            } else {
                currentQueue.nextHeap = thisHeap;
                thisHeap.previousHeap = currentQueue;
                maxRank = Math.max(maxRank, thisHeap.root.rank);
                thisHeap = thisHeap.nextHeap;
            }

            currentQueue = currentQueue.nextHeap;   
        }
        // Append all the missing binary heaps
        if (thisHeap != null) {
            currentQueue.nextHeap = thisHeap;
            thisHeap.previousHeap = currentQueue;
        } else {
            currentQueue.nextHeap = otherHeap;
            otherHeap.previousHeap = thisHeap;
        }

        // Next we combine heaps of the same rank
        int currentRank = 1;
        // Reset to start of linked list
        currentQueue = root;
        while (currentQueue.nextHeap != null) {
            if (currentQueue.root.rank == currentQueue.nextHeap.root.rank) {
                // Only merge two trees if there are not 3 of the same kind
                if (currentQueue.nextHeap.nextHeap == null ||
                    currentQueue.nextHeap.root.rank != currentQueue.nextHeap.nextHeap.root.rank) {
                    BinaryHeap newHeap = combine(currentQueue, currentQueue.nextHeap);
                    newHeap.previousHeap = currentQueue.previousHeap;
                    currentRank = Math.max(currentRank, newHeap.root.rank);
                    if (newHeap.previousHeap == null) {
                        root = newHeap;
                    } else {
                        currentQueue.nextHeap = newHeap;
                    }
                }
            // we only need to look at trees smaller than maxRank + 1
            // because all trees with higher rank can only be part of one
            // of the list of heaps.
            } else if (currentQueue.root.rank > maxRank + 1) {
                break;
            }
            currentQueue = currentQueue.nextHeap;
            rank = Math.max(rank, currentRank);
        }

        queue = root;
        corruptedElements.addAll(other.corruptedElements);
        size += other.size();
        currentQueue.updateSuffixMin();

        return this;
    }


    // Yeah, Stringbuilder would have been better. On the other hand, this is only for small testing purposes
    // and will be removed once the project is finished.
    public String toString() {
        String ret = "";
        for (BinaryHeap heap = queue; heap != null; heap = heap.nextHeap) {
            ret += heap.root;
        }
        return ret;
    }

    protected class BinaryHeap {
        BinaryHeapNode root;
        BinaryHeap nextHeap, previousHeap;
        BinaryHeap sufMin;

        public BinaryHeap() {
            root = new BinaryHeapNode();
        }

        public BinaryHeap(BinaryHeapNode root) {
            this.root = root;
        }

        /**
         * Updates sufMin, which points to the tree following this tree
         * with the smallest root node. 
         */
        public void updateSuffixMin() {
            if (nextHeap != null && comparator.compare(root.key, nextHeap.sufMin.root.key) > 0) {
                sufMin = nextHeap.sufMin;
            } else {
                sufMin = this;
            }
            if (previousHeap != null) {
                previousHeap.updateSuffixMin();
            }
        }
    }

    protected class BinaryHeapNode {
        BinaryHeapNode rightChild, leftChild;
        List<T> nodeElements;
        T key;
        final int rank;
        final int size;
        int index;

        public BinaryHeapNode(BinaryHeapNode leftChild, BinaryHeapNode rightChild, T key, int rank) {
            this.key = key;
            this.rank = rank;
            this.rightChild = rightChild;
            this.leftChild = leftChild;
            if (rank < nodeTargetSize) {
                size = 1;
            } else {
                size = (int)Math.ceil(3 * Math.max(leftChild.size, rightChild.size) / 2);
            }
            nodeElements = new ArrayList<>();
            sift();
        }

        public BinaryHeapNode() {
            rank = 1;
            size = 1;
            nodeElements = new ArrayList<>();
        }

        /**
         * Inserts the given element into the selected node.
         * @param element the element to be inserted
         * @throws IllegalArgumentException in case the key is too large
         */
        public void insert(T element) {
            if (key == null) {
                key = element;
            } else if (comparator.compare(element, key) > 0) {
                throw new IllegalArgumentException(String.format("element %s is too large for %s", element, key));
            }
            nodeElements.add(element);
        }

        /**
         * Returns whether a node is a leaf, which is the case
         * if both of its children don't exist.
         * @return whether the node is a leaf
         */
        public boolean isLeaf() {
            return rightChild == null && leftChild == null;
        }


        /**
         * Replenishes the elements associated with the given key
         * if the amount of elements in the List {@link nodeElements}
         * has dropped below size / 2.
         * @return the number of elements now contained in the list
         */
        public int sift() {
            while (nodeElements.size() < size / 2 && !isLeaf()) {
                // At least the left child exists.
                // Now we check whether the right child exists and
                // swap both if the key of the rightChild is smaller
                if (rightChild != null) {
                    if (comparator.compare(leftChild.key, rightChild.key) > 0) {
                        BinaryHeapNode smallerChild = rightChild;
                        rightChild = leftChild;
                        leftChild = smallerChild;
                    }
                }
                // Update the set keeping track of the corrupted elements
                corruptedElements.addAll(leftChild.nodeElements);
                // Append all elements from the child node
                key = leftChild.key;
                nodeElements.addAll(leftChild.nodeElements);
                leftChild.nodeElements.clear();
                
                // Recurse and delete the child if it appears to be empty
                if (leftChild.sift() == 0) {
                    leftChild = rightChild;
                    rightChild = null;
                }
            }
            return nodeElements.size();
        }

        public String toString() {
            return String.join("", Collections.nCopies(rank, " ")) + String.format("%s, %s, %s: ", rank, size, key) + nodeElements.stream().map(Object::toString).collect(Collectors.joining("[", ", ","]")) + " -> \n" + leftChild + "\n" + rightChild + "\n";
        }
    }
}