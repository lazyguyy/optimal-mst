package util.queue;

import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.LinkedList;
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
        queue = new BinaryHeap();
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
        return elements.get(0);
    }

    @Override
    public T pop() {
        BinaryHeap minHeap = queue.sufMin;
        List<T> elements = minHeap.root.nodeElements;
        T element = elements.get(0);
        elements.remove(0);
        if (minHeap.root.size * 0.5 > elements.size()) {
        	// This binary heap does not contain any elements at all
        	if (minHeap.root.sift() == 0) {
                if (minHeap.nextHeap != null) {
                    minHeap.nextHeap.previousHeap = minHeap.previousHeap;
                }
                if (minHeap.previousHeap == null) {
                    queue = minHeap.nextHeap;
                } else {
                    minHeap.previousHeap.nextHeap = minHeap.nextHeap;
                    minHeap.previousHeap.updateSuffixMin();
                }
        	} else {
        		minHeap.updateSuffixMin();
        	}
        }
        corruptedElements.remove(elements);
        size--;
        return element;
    }

    @Override
    public void insert(T element) {
        SoftHeap<T> newHeap = new SoftHeap<T>(errorRate, comparator, element);
        meld(newHeap);
    }

    private BinaryHeap combine(BinaryHeap first, BinaryHeap second) {
        BinaryHeapNode newRoot = new BinaryHeapNode(first.root, second.root, null, first.root.rank + 1);
        BinaryHeap newHeap = new BinaryHeap(newRoot);
        if (second.nextHeap != null) {
        	second.nextHeap.previousHeap = newHeap;
        }
        newHeap.previousHeap = first.previousHeap;
        newHeap.nextHeap = second.nextHeap;
        first.nextHeap = null;
        second.previousHeap = null;
        return newHeap;
    }
    
    public void reset() {
    	this.queue = null;
    	this.size = 0;
    	this.rank = 0;
    	this.corruptedElements.clear();
    }

    @Override
    public SoftHeap<T> meld(SoftHeap<T> other) {
    	if (this.queue == null) {
    		this.queue = other.queue;
    		this.rank = other.rank;
    		this.corruptedElements = other.corruptedElements;
    		this.size = other.size;
    		queue.updateSuffixMin();
    		other.reset();
    		return this;
    	}
        BinaryHeap thisHeap = this.queue, otherHeap = other.queue;
        BinaryHeap currentHeap;
        int maxRank = 1;
        // First we merge both Lists of binary heaps and keep them
        // in non-decreasing order of ranks
        // Decide from which queue to take first
        if(otherHeap.root.rank < thisHeap.root.rank) {
            currentHeap = otherHeap;
            otherHeap = otherHeap.nextHeap;  
        } else {
            currentHeap = thisHeap;
            thisHeap = thisHeap.nextHeap;
        }
        // Keeps track of the root of the linked list of binary heaps
        BinaryHeap root = currentHeap;
        // Merge as long as we have not reached the end of one of the queues
        while (thisHeap != null && otherHeap != null) {
            if (otherHeap.root.rank < thisHeap.root.rank) {
                currentHeap.nextHeap = otherHeap;
                otherHeap.previousHeap = currentHeap;
                maxRank = Math.max(maxRank, otherHeap.root.rank);
                otherHeap = otherHeap.nextHeap;
            } else {
                currentHeap.nextHeap = thisHeap;
                thisHeap.previousHeap = currentHeap;
                maxRank = Math.max(maxRank, thisHeap.root.rank);
                thisHeap = thisHeap.nextHeap;
            }

            currentHeap = currentHeap.nextHeap;   
        }
        // Append all the missing binary heaps
        if (thisHeap != null) {
            currentHeap.nextHeap = thisHeap;
            thisHeap.previousHeap = currentHeap;
        } else {
            currentHeap.nextHeap = otherHeap;
            otherHeap.previousHeap = currentHeap;
        }

        // Next we combine heaps of the same rank
        // Reset to start of linked list
        currentHeap = root;
        while (currentHeap.nextHeap != null) {
            if (currentHeap.root.rank == currentHeap.nextHeap.root.rank) {
                // Only merge two trees if there are not 3 of the same kind
                if (currentHeap.nextHeap.nextHeap == null ||
                    currentHeap.nextHeap.root.rank != currentHeap.nextHeap.nextHeap.root.rank) {
                    BinaryHeap newHeap = combine(currentHeap, currentHeap.nextHeap);
                    rank = Math.max(rank, newHeap.root.rank);
                    if (newHeap.previousHeap == null) {
                        root = newHeap;
                        currentHeap = newHeap;
                        continue;
                    } else {
                        currentHeap.nextHeap = newHeap;
                    }
                }
            // we only need to look at trees smaller than maxRank + 1
            // because all trees with higher rank can only be part of one
            // of the list of heaps.
            } else if (currentHeap.root.rank > maxRank) {
                break;
            }
            currentHeap = currentHeap.nextHeap;
            rank = Math.max(rank, maxRank);
        }

        queue = root;
        corruptedElements.addAll(other.corruptedElements);
        size += other.size();
        currentHeap.updateSuffixMin();
        other.reset();

        return this;
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
            } else {
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
                size = (int)Math.ceil(3 * Math.max(leftChild.size, rightChild.size) *0.5);
            }
            nodeElements = new LinkedList<>();
            sift();
        }

        public BinaryHeapNode() {
            rank = 1;
            size = 1;
            nodeElements = new LinkedList<>();
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
            while (nodeElements.size() < size * 0.5 && !isLeaf()) {
//            	System.out.println(nodeElements + ", " + leftChild + " : " + rightChild);
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
    }
}