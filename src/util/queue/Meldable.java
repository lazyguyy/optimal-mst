package util.queue;

public interface Meldable<T> {
    /**
     * Destructively melds other into this.
     *
     * @param other the element to meld into this
     */
    void meld(T other);
}
