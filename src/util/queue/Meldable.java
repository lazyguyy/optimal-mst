package util.queue;

public interface Meldable<T> {
    Meldable<T> meld(T other);
}
