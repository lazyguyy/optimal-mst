package test;

import util.queue.KAryHeap;
import util.queue.PriorityQueue;
import util.queue.LossyPriorityQueue;
import util.queue.SoftHeap;

public class HeapTest {

    public static void main(String[] args) {
        PriorityQueue<Integer> pq = KAryHeap.naturallyOrdered(2);
        pq.insert(5);
        pq.insert(7);
        pq.insert(2);
        System.out.println(pq.pop());
        System.out.println(pq.pop());
        System.out.println(pq.pop());

        LossyPriorityQueue<Integer> lpq = SoftHeap.naturallyOrdered(0.25);

        int[] elements = {1,3,2,5,6,0,-1,10};
        for (int i : elements) {
            lpq.insert(i);
            System.out.println(lpq);
        }
    }
}
