package Domain.Utility;

public class Tuple<T0, T1> {
    private final T0 first;
    private final T1 second;

    public Tuple(T0 first, T1 second) {
        this.first = first;
        this.second = second;
    }

    public T0 getFirst() {
        return first;
    }

    public T1 getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}