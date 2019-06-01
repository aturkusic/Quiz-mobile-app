package ba.unsa.etf.rma.ostalo;

import java.util.Comparator;

import ba.unsa.etf.rma.klase.Rang;

public class RangComparator implements Comparator<Trojka<Integer, String, Double>>
{
    public int compare(Trojka<Integer, String, Double> left, Trojka<Integer, String, Double> right) {
        if(left.getThird() < right.getThird())
            return 1;
        else if(right.getThird() < left.getThird())
            return -1;
        return 0;
    }
}