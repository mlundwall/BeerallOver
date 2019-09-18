package com.lundwall.beerallover;

/**
 * Created by mluc on 15-03-2016.
 */
public class ListeObj {
    int Id = -1;
    String Navn = "";
    Double Pct = -1.0;
    Double Rating = -1.0;
    String OlType = "";

    public ListeObj(int i, String n, Double p, Double r, String t) {
        Id = i;
        Navn = n;
        Pct = p;
        Rating = r;
        OlType = t;
    }

}
