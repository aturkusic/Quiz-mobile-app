package ba.unsa.etf.rma.ostalo;

public class Trojka<T, U, V>  {
    private  T first;
    private  U second;
    private  V third;

    public Trojka(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }

    public void setFirst(T vrijednost) { first = vrijednost; }
}
