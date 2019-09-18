/**
 * Created by DFH on 06-03-16.
 * Implementerer en serie særlige læser, der læse JSON indhold fra en strøm
 * OG koerUrl, der læser op til 4 tegn retur fra en internetkald
 *
 *   public ArrayList laesLande(String s) throws IOException {
 *     public LandObj laesEtLand(JsonReader reader) throws IOException {
 *   public ArrayList laesListe(String s) throws IOException {
 *     public ListeObj laesEnListe(JsonReader reader) throws IOException {
 *   public ReviewObj laesReview(String s) throws IOException {
 *   public OlObj laesOl(String s) throws IOException {
 *     public OlObj laesEnOl(JsonReader reader) throws IOException {
 *   public String koerUrl(String s) throws IOException {
 * Kaldes fra InternetTask.java
 */

package com.lundwall.beerallover;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import static com.lundwall.beerallover.CommonData.*;

public class JsonLaeser {

    public ArrayList laesLande(String s) throws IOException {
        JsonReader reader = new JsonReader((new InputStreamReader(new URL(s).openStream(), StandardCharsets.UTF_8)));
        ArrayList alleLande = new ArrayList();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                alleLande.add(laesEtLand(reader));
            }
            reader.endArray();
        }
        finally {
            reader.close();
        }
        return alleLande;
    }

    public LandObj laesEtLand(JsonReader reader) throws IOException {
    int id = -1;
    String navn = null;
    String kode = null;

        reader.beginObject();
        reader.nextName();
        id = reader.nextInt();
        reader.nextName();
        navn = reader.nextString();
        reader.nextName();
        kode = reader.nextString();
        reader.endObject();
        return new LandObj(id, navn, kode);
    }

    public ArrayList laesListe(String s) throws IOException {
    JsonReader reader = new JsonReader((new InputStreamReader(new URL(s).openStream(), StandardCharsets.UTF_8)));
    ArrayList alleListe = new ArrayList();

        try {
            reader.beginArray();
            while (reader.hasNext()) {
                alleListe.add(laesEnListe(reader));
            }
            reader.endArray();
        }
        finally {
            reader.close();
        }
        return alleListe;
    }

    public ListeObj laesEnListe(JsonReader reader) throws IOException {
        int id        = -1;
        String navn   = null;
        Double pct    = 0.0;
        Double rating = 0.0;
        String type   = "";

        reader.beginObject();
        reader.nextName();
        id = reader.nextInt();
        reader.nextName();
        navn = reader.nextString();
        reader.nextName();
        try {
            pct = reader.nextDouble();
        } catch (Exception e) {
            reader.nextNull();
            pct = 0.0;
        }
        reader.nextName();
        try {
            rating = reader.nextDouble();
        } catch (Exception e) {
            reader.nextNull();
            rating = 0.0;
        }
        reader.nextName();
        type = reader.nextString();
        reader.endObject();
        return new ListeObj(id, navn, pct, rating, type);
    }

    public ReviewObj laesReview(String s) throws IOException {
        JsonReader reader = new JsonReader((new InputStreamReader(new URL(s).openStream(), StandardCharsets.UTF_8)));
        int rating   = 0;
        String comment  = "";
        try {
            reader.beginArray();
            if (reader.hasNext()) {
                reader.beginObject();
                reader.nextName();
                rating = reader.nextInt();
                reader.nextName();
                comment = reader.nextString();
                reader.endObject();
                reader.endArray();
                return new ReviewObj(rating,comment);
            }
            else
                return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public OlObj laesOl(String s) throws IOException {
        JsonReader reader = new JsonReader((new InputStreamReader(new URL(s).openStream(), StandardCharsets.UTF_8)));
        OlObj o;

        try {
            return laesEnOl(reader);
        }
        finally {
            reader.close();
        }
    }

    public OlObj laesEnOl(JsonReader reader) throws IOException {
        String navn     = null;
        Double pct      = 0.0;
        String type     = "";
        Double rating   = 0.0;
        String comment  = "";
        Bitmap billede  = null;
        String bryggeri = "";

        reader.beginArray();
        if (reader.hasNext()) {
            reader.beginObject();
            reader.nextName();
            navn = reader.nextString();
            reader.nextName();
            try { pct = reader.nextDouble(); } catch (Exception e) { reader.nextNull(); pct=-1.0; }
            reader.nextName();
            type = reader.nextString();
            reader.nextName();
            try { rating = reader.nextDouble(); } catch (Exception e) { reader.nextNull(); rating=0.0; }
            reader.nextName();
            comment = reader.nextString();
            reader.nextName();
            bryggeri = reader.nextString();
            reader.endObject();
            reader.endArray();

            //reader.nextName();
            //  String s= reader.nextString();
            //  billede = s.getBytes("ISO_8859_1");
            //  ISO_8859_1 er nødvendig så den ikke bruger 2 byte pr karakter
            //  billede = reader.nextString().getBytes("ISO_8859_1");

            String olbillede = rodUrl+billedurl+"pic-"+ olId +".jpg";
            try {
                URL aURL = new URL(olbillede);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                billede = BitmapFactory.decodeStream(bis);
                bis.close();
            } catch (Exception e) {
                return null;
            }
        }
        return new OlObj(navn, pct, type, rating, comment, billede, bryggeri);
    }


    public String koerUrl(String url) throws IOException {
    String s;

        try {
            URL u = new URL(url);
            char[] r = "    ".toCharArray();
            InputStreamReader Laeser = new InputStreamReader(u.openStream(), StandardCharsets.UTF_8);
            Laeser.read(r);
            s = String.valueOf(r).replace('\n', ' ').replace('\r', ' ').trim();
        }
        catch (Exception e) {
            return null;
        }
        return s;
    }

}