/**
 * Fælles data for helle "pakken"
 * Created by mluc on 17-03-2016.
 */

package com.lundwall.beerallover;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import java.util.ArrayList;
import com.lundwall.beerallover.db;

public final class CommonData {

    // Constanter
    static final String rodUrl          = "http://kolibri.lundwall.dk/"; // Eller "https://beerallover.com/json/";
    static final String inifile         = "bao.ini";
    static final String reviewphp       = "bao.review.php";
    static final String olphp           = "bao.ol.php";
    static final String listephp        = "bao.liste.php";
    static final String gemratingphp    = "bao.gemrating.php";
    static final String billedurl       = "bao.pict/";

    // GPS Disablet
    // GPSTracker.java :
//    static final String gps_site        = "http://api.geonames.org/countryCode";
//    static final String gps_user        = "mlundwall";

    // Settings
    static Boolean      setup_skaldytte = true;
// GPS Disablet
    static Boolean      setup_fragps    = false; // Disablet
//  static Boolean      setup_fragps    = true;  // Enablet

    // Arbejdsvariable
    static String       uid             = "";    // Unik brugerID pr telefon
    static String       landenavn       = "";    // Aktuelt landenavn
    static int          landeId         = -1;    // Aktuelt landeId
    static int          gemLandeId      = -1;    // Sidst valgte landeId
    static int          landIdB         = -1;    // Det land, der pt er gemt i lokal database

    static int          olId            = -1;    // Aktuelt Øl Id
    static int          gemolId         = -1;    // Sidst valgte øl
    static ArrayList    olListeArray    = null;  // Liste over øl i aktuelt land
    static OlObj        olObj           = null;  // Al info om en øl

    static ArrayList    landeArray      = null;  // Alle lande

    static Boolean      dyttet          = false; // Om der er blevet PFFFT'et  i denne session
    static int          lokalrating     = -1;    // Brugervalgt rating - der evt vil blive gemt
    static ReviewObj    brugerReview    = null;  // Aktuelt review

    // Views
    static View         findView        = null;
    static View         listView        = null;
    static View         olView          = null;
    static View         reView          = null;
    static db           dataBase        = null;

}