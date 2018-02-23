package util;

public interface Meldable<T> {
    Meldable<T> meld(Meldable<? extends T> other);
}
