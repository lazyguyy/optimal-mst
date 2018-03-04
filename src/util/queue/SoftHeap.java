package util.queue;

import java.util.*;

public class SoftHeap<T> implements SoftPriorityQueue<T>, Meldable<SoftHeap<T>> {

    private final double errorRate;
    private final int nodeMinRank;
    private BinaryHeap queue;
    private Set<T> corruptedElements;
    private final Comparator<? super T> comparator;
    private int size;
    private int rank;

    
    public SoftHeap(double errorRate, Comparator<? super T> comparator, T element) {
        this(errorRate, comparator);
        queue = new BinaryHeap();
        queue.root.insert(element);
        queue.sufMin = queue;
        size = 1;
        rank = 1;
        corruptedElements = new HashSet<>();
    }

    public SoftHeap(double errorRate, Comparator<? super T> comparator) {
        this.errorRate = errorRate;
        // Cause Java doesn't support computing the logarithm to an arbitrary base...
        nodeMinRank = (int)Math.ceil(Math.log(1 / errorRate) / Math.log(2)) + 5;
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
    public Collection<T> corrupted() {
        return corruptedElements;
    }

    @Override
    public T peek() {
        List<? extends T> elements = queue.sufMin.root.elements;
        return elements.get(0);
    }

    @Override
    public T pop() {
        BinaryHeap minHeap = queue.sufMin;
        List<T> elements = minHeap.root.elements;
        T element = elements.get(0);
        elements.remove(0);
        if (minHeap.root.size * 0.5 > elements.size()) {
        	// Replenish the list.
        	// Remove the tree if its now empty
        	if (minHeap.root.sift() == 0) {
                removeHeap(minHeap);
        	} else {
        		minHeap.updateSuffixMin();
        	}
        }
        corruptedElements.remove(element);
        size--;
        return element;
    }

    @Override
    public void insert(T element) {
        SoftHeap<T> newHeap = new SoftHeap<T>(errorRate, comparator, element);
        meld(newHeap);
    }

    /**
     * Combines two binary heaps
     * @param first the first heap
     * @param second the second heap
     * @return the combined heap
     */
    private BinaryHeap combine(BinaryHeap first, BinaryHeap second) {
    	// Update first so its root node contains first and second as childs.
    	// We keep the references for now
        first.combine(second);
        return first;
    }
    /**
     * Removes a heap from the queue. Updates all pointers properly
     * @param heap the heap to be removed
     */
    private void removeHeap(BinaryHeap heap) {
    	if (heap.next != null) {
    		heap.next.prev = heap.prev;
    	}
    	// the heap we are removing was the start of the queue
    	if (heap.prev == null){
    		queue = heap.next;
    	} else {
    		heap.prev.next = heap.next;
    		heap.prev.updateSuffixMin();
    	}
    }
    /**
     * Resets the binary heap
     */
    public void clear() {
    	this.queue = null;
    	this.size = 0;
    	this.rank = 0;
    	this.corruptedElements.clear();
    }
    /**
     * Merges two queues of Binary heaps and keeps the heaps in increasing order by rank
     * @param queue1 the first queue 
     * @param queue2 the second queue
     * @return the first heap in the queue of merged binary heaps
     */
    private BinaryHeap mergeQueues(BinaryHeap queue1, BinaryHeap queue2) {
    	BinaryHeap root;
    	// Find the Heap with smallest rank and put it at the very beginning of the merged queue
    	if (queue1.root.rank < queue2.root.rank) {
    		root = queue1;
    		queue1 = queue1.next;
    	} else {
    		root = queue2;
    		queue2 = queue2.next;
    	}
    	BinaryHeap currentHeap = root;
    	// While there are elements in both queues, take the smaller one and append it to our queue
    	while (queue1 != null && queue2 != null) {
    		BinaryHeap smallerHeap;
    		if (queue1.root.rank < queue2.root.rank) {
    			smallerHeap = queue1;
    			queue1 = queue1.next;
    		} else {
    			smallerHeap = queue2;
    			queue2 = queue2.next;
    		}
    		currentHeap.next = smallerHeap;
    		smallerHeap.prev = currentHeap;
    		currentHeap = currentHeap.next;
    	}
    	BinaryHeap remainingQueue;
    	// Either queue1 or queue2 still contains elements, and because the heaps are ordered in increasing order by rank
    	// we can just append the remaining heaps
    	if (queue1 != null) {
    		remainingQueue = queue1;
    	} else {
    		remainingQueue = queue2;
    	}
    	currentHeap.next = remainingQueue;
    	remainingQueue.prev = currentHeap;
    	
    	return root;
    }
    
    /**
     * Combines all heaps of the same rank so that there is only one heap for a given rank
     * @param queue the queue in which to combine all trees
     * @param combineUpTo the rank up to which duplicates of a rank can appear
     * @return the last which suffix min pointer needs to be updated
     */
    private BinaryHeap repeatedCombine(BinaryHeap queue, int combineUpTo) {
    	BinaryHeap currentHeap = queue;
    	while (currentHeap.next != null) {
    		// Combine two trees if they are of the same rank
    		if (currentHeap.root.rank == currentHeap.next.root.rank) {
    			// But only if there is not a third of the same rank following these two
    			if (currentHeap.next.next == null ||
    					currentHeap.root.rank != currentHeap.next.next.root.rank) {
    				currentHeap = combine(currentHeap, currentHeap.next);
    				rank = Math.max(rank, currentHeap.root.rank);
    				removeHeap(currentHeap.next);
    				continue;
    			}
    		} else if(currentHeap.root.rank > combineUpTo) {
    			break;
    		}
    		currentHeap = currentHeap.next;
    	}
    	return currentHeap;
    }

    @Override
    public void meld(SoftHeap<T> other) {
    	if (other.queue == null) {
    		return;
    	}
    	// In case this soft heap is empty we just copy the other heap
    	if (this.queue == null) {
    		this.queue = other.queue;
    		this.rank = other.rank;
    		this.corruptedElements = other.corruptedElements;
    		this.size = other.size;
    		other.clear();
    		return;
    	}
    	int combineUpTo = Math.min(this.rank, other.rank);
    	rank = Math.max(this.rank, other.rank);
    	// Merge both queues in increasing order of rank
    	queue = mergeQueues(this.queue, other.queue);

        // Next we combine heaps of the same rank k to a single heap of rank k + 1
        BinaryHeap lastUpdatedHeap = repeatedCombine(queue, combineUpTo);
        // Update the set of corrupted elements as well as size and the sufMin pointers
        corruptedElements.addAll(other.corruptedElements);
        size += other.size();
        lastUpdatedHeap.updateSuffixMin();
        // clear the other heap, because it has been destroyed in the merging process
        other.clear();
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("SoftHeap of rank ").append(rank).append(" with ").append(size).append(" elements\n");
    	for (BinaryHeap heap = queue; heap != null; heap = heap.next) {
    		sb.append(heap.prev).append(" - ").append(heap).append(" - ").append(heap.next).append("\n");
    		sb.append("Suffix-Min points to ").append(heap.sufMin).append("\n");
    	}
    	return sb.toString();
    }

    protected class BinaryHeap {
        BinaryHeapNode root;
        BinaryHeap next, prev;
        BinaryHeap sufMin;

        public BinaryHeap() {
            root = new BinaryHeapNode();
        }

        public void combine(BinaryHeap second) {
            root = new BinaryHeapNode(root, second.root, null, second.root.rank + 1);
        }

        /**
         * Updates sufMin, which points to the tree following this tree
         * with the smallest root node. 
         */
        public void updateSuffixMin() {
            if (next != null && comparator.compare(root.key, next.sufMin.root.key) > 0) {
                sufMin = next.sufMin;
            } else {
                sufMin = this;
            }
            if (prev != null) {
                prev.updateSuffixMin();
            } 
        }
        public String toString() {
        	return "R " + root.rank + " | S " + root.size;
        }
    }

    protected class BinaryHeapNode {
        BinaryHeapNode rightChild, leftChild;
        List<T> elements;
        T key;
        final int rank;
        final int size;
        int index;

        public BinaryHeapNode(BinaryHeapNode leftChild, BinaryHeapNode rightChild, T key, int rank) {
            this.key = key;
            this.rank = rank;
            this.rightChild = rightChild;
            this.leftChild = leftChild;
            if (rank < nodeMinRank) {
                size = 1;
            } else {
                size = (int)Math.ceil(3 * Math.max(leftChild.size, rightChild.size) * 0.5);
            }
            elements = new LinkedList<>();
            sift();
        }

        public BinaryHeapNode() {
            rank = 1;
            size = 1;
            elements = new LinkedList<>();
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
            elements.add(element);
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
         * if the amount of elements in the list
         * has dropped below size / 2.
         * @return the number of elements now contained in the list
         */
        public int sift() {
            while (elements.size() < size * 0.5 && !isLeaf()) {
//            	System.out.println(elements + ", " + leftChild + " : " + rightChild);
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
                corruptedElements.addAll(elements);
                // Append all elements from the child node
                key = leftChild.key;
                elements.addAll(leftChild.elements);
                leftChild.elements.clear();
                
                // Recurse and delete the child if it appears to be empty
                if (leftChild.sift() == 0) {
                    leftChild = rightChild;
                    rightChild = null;
                }
            }
            return elements.size();
        }
    }
}