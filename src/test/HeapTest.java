package test;

import util.queue.KAryHeap;
import util.queue.PriorityQueue;

public class HeapTest {

    public static void main(String[] args) {
        PriorityQueue<Integer> pq = KAryHeap.naturallyOrdered(2);
        pq.insert(5);
        pq.insert(7);
        pq.insert(2);
        System.out.println(pq.pop());
        System.out.println(pq.pop());
        System.out.println(pq.pop());
    }
}
