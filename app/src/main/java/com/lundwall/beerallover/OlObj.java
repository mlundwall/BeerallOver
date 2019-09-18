package com.lundwall.beerallover;

import android.graphics.Bitmap;

/**
 * Created by mluc on 15-03-2016.
 */
public class OlObj {
    String Navn = "";
    Double Pct = -1.0;
    String OlType = "";
    Double Rating = 0.0;
    String Comment = "";
    Bitmap Billede;
    String Bryggeri = "";

    public OlObj(String n, Double p, String t, Double r, String c, Bitmap bi, String br) {
        Navn = n;
        Pct = p;
        OlType = t;
        Rating = r;
        Comment = c;
        Billede = bi;
        Bryggeri = br;
    }

}
