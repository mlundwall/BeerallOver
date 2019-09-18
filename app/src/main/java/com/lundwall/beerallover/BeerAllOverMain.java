/**

 Mads Lundwall startet februar 2016.

 Version 28/6-2017
 -----------------

 TODO
 *)  Når vi mister forbindelsen skal listen køres ned til een valmulighed (den gemte)
 *)  Når forbindelsen kommer igen skal listen foldes ud med alle lande
 *)  Går ned når der er givet anmeldelse :
 Løst ved at fjerne bruge den rigtige contekst i v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
 *)  Vis landenavn et eller andet sted på Liste, Øl og Review-siderne
 *)  Den første må ikke vælges - eller også skal den hentes (Nej)
 Der skal stå tom øl og intet review
 *)  Danske bogstaver i kommentarer. Accepterer lige nu %20 (replace)
 *)  Alle tekster til over i "strings"

 OPGAVER
 *)  Gem land incl liste, øl og review i en "fil"
 *)  Flyt alle billeder ud i filer

 NOTER
 Genintroducer get(10, TimeUnit.SECONDS); Hér og i GPSTracker.java - Næppe

 Conditionel kompilering
 -----------------------
 Led efter "GPS disablet" og lav Java betingede udførsler med
 if (GPSon) { dytbåt }

 Vi kan have forskellige view afhængig at kilde:
 R.layout kan gøres afhængig af

 final boolean GPSon = false (eller "= true" natürlich)

 if (GPSon)
 rootView = inflater.inflate(R.layout.findGPS, container, false);
 else
 rootView = inflater.inflate(R.layout.find, container, false);

 Særligt i onCreateView

 */

package com.lundwall.beerallover;

import static com.lundwall.beerallover.CommonData.*;
import static com.lundwall.beerallover.CommonData.landeId;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.*;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentManager;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.tabs.TabLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Hovedaktivitet
 */
public class BeerAllOverMain extends AppCompatActivity {

  /**
   * The {@link PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * Her opbevares aktuel context
   */
  public static Context BAPcontext;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private static ViewPager mViewPager;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  /**
   * Genererer Psudo-unique ID
   *
   * @return En tekstreng uden mellemrum
   */
  public static String getUniquePsuedoID() {
    // If all else fails, if the user does have lower than API 9 (lower
    // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
    // returns 'null', then simply the ID returned will be solely based
    // off their Android device information. This is where the collisions
    // can happen.
    // Thanks http://www.pocketmagic.net/?p=1662!
    // Try not to use DISPLAY, HOST or ID - these items could change.
    // If there are collisions, there will be overlapping data
    String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

    // Thanks to @Roman SL!
    // http://stackoverflow.com/a/4789483/950427
    // Only devices with API >= 9 have android.os.Build.SERIAL
    // http://developer.android.com/reference/android/os/Build.html#SERIAL
    // If a user upgrades software or roots their device, there will be a duplicate entry
    String serial = null;
    try {
      serial = android.os.Build.class.getField("SERIAL").get(null).toString();

      // Go ahead and return the serial for api => 9
      return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    } catch (Exception exception) {
      // String needs to be initialized
      serial = "serial"; // some value
    }

    // Thanks @Joe!
    // http://stackoverflow.com/a/2853253/950427
    // Finally, combine the values we have found by using the UUID class to create a unique identifier
    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
  }

  /**
   * Dytter
   */
  protected static void dyt() {
    MediaPlayer mp = MediaPlayer.create(BAPcontext, R.raw.pft);
    mp.start();
    dyttet = true;
  }

  /**
   * Leder i landelisten efter land med koden lk.
   * Returnerer tom hvis ikke fundet
   */
  // GPS disablet
/*
    private static String findland() {
        String ln = "";
        String lk = "";

        // Forsøger at lokalisere mobilen:
        GPSTracker gps = new GPSTracker(BAPcontext);
        if (gps.canGetLocation()) {
            lk = gps.getCountry();
            if (lk != "") {
                LandObj landObj = null;
                // Vi leder efter Kode som vi har i lk
                int i = 0;
                while (i < landeArray.size()) {
                    landObj = (LandObj) landeArray.get(i);
                    if (landObj.Kode.equals(lk)) {
                        ln = landObj.Navn;
                        i = landeArray.size();
                    }
                    i++;
                }
            }
        };
        return (ln);
    }
*/

/**
 *  Afsnit:
 *  Util funktioner
 */

  /**
   * Har vi internet
   */
  private static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) BAPcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
  }

  /**
   * Er der gemt et land i databasen
   */
  private static boolean dataiDb(){
    return landIdB != -1;
  }

  /**
   * Er det det aktuelle land der er i databasen
   */
  private static boolean aktueltLandGemtiDb(){
    return (landeId > 0) && (landIdB == landeId);
  }

  /**
   * Er det et nyt land
   */
  private static boolean nytLand(){
    return  (landeId==-1) || (gemLandeId != landeId);
  }

  /**
   * Er det en ny Øl
   */
  private static boolean nyOl(){
    return gemolId != olId;
  }

  /**
   * Viser/Skjuler
   * Hvis aktuelt land er gemt vises (saved) label og gemmer GemLandKnap
   * Modsat hvis land ikke er gemt
   */
  private static void visOmErGemt(View v) {
    TextView ergemtlabel = v.findViewById(R.id.ergemt);
    Button GemLandKnap = v.findViewById(R.id.GemLandKnap);

    if (aktueltLandGemtiDb()) {
      ergemtlabel.setText(BAPcontext.getString(R.string.Saved));
      GemLandKnap.setVisibility(View.INVISIBLE);
    }
    else {
      ergemtlabel.setText("");
      GemLandKnap.setVisibility(View.VISIBLE);
    }
  }

  /**
   *  Er vi ikke forbundet, skal vi filde landeId via landenavn
   */
  protected static String findlandeId(int id) {
    LandObj landObj;
    if (landeArray!=null && landeArray.size()>0)
      for (int i = 0; i < landeArray.size(); i++) {
        landObj = (LandObj) landeArray.get(i);
        if (landObj.Id==id)
          return(landObj.Navn);
      }
    return "";
  }

  /**
   * Henter landenavn og setupvariable fra filen bao.ini
   * Hvis den ikke findes laves den.
   * Hvis setup_fragps er sat, hentes land fra GPS
   * Hvis setup_skaldytte dyttes
   */
  protected static void hentDataOgInit() {
    String ln = "";
    File file = new File(BAPcontext.getFilesDir(), inifile);

    if (file.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        ln = br.readLine();
        setup_skaldytte = br.readLine().equals("true");
        setup_fragps = br.readLine().equals("true");
        landIdB = Integer.parseInt(br.readLine());
        br.close();
      } catch (Exception e) {
        ln = "";
        setup_skaldytte = true;
        setup_fragps = false;
        landIdB = -1;
      }
    }
    // Hvis vi skal hente fra GPS og vi er forbundet
    // GPS disablet
        /*
        if (isConnected() && (setup_fragps || ln.equals(""))) {
            // Landet findes via GPS
            landenavn = findland();
        }
        else
        */
    {
      if (isConnected())
        landenavn = ln;
      else
      {
        // Er vi ikke forbundet, skal vi filde landenavn via landIdB
        landenavn = findlandeId(landIdB);
        landeId   = landIdB;
      }
    }
    // Hvis vi skal dytte
    if (setup_skaldytte && !dyttet)
      dyt();
  }

  /**
   * Gemmer landenavn i filen bao.ini
   */
  protected static void gemData() {
    // ML Skriver til fil
    File mitdir = BAPcontext.getFilesDir();
    File file = new File(mitdir, inifile);
    try {
      if (!file.exists())
        file.createNewFile();
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      // Skriver variable een pr linie
      bw.write(landenavn);
      bw.newLine();
      if (setup_skaldytte) bw.write("true");
      else bw.write("false");
      bw.newLine();
      if (setup_fragps) bw.write("true");
      else bw.write("false");
      bw.newLine();
      bw.write(Integer.toString(landIdB));
      bw.newLine();
      // Lukker pænt
      bw.close();
    } catch (Exception e) {
      if (BuildConfig.DEBUG)
        Toast.makeText(BAPcontext, "FEJL 1 Gemdata " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_beer_all_over_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);
    // ML Vi husker alle fire sider: find, liste, ol og rewiev
    mViewPager.setOffscreenPageLimit(4);

    TabLayout tabLayout = findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    // -------------------------------
    // ML: Initierer mine systemvariable:
    // -------------------------------
    BAPcontext = getApplicationContext();

    uid = getUniquePsuedoID();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_beer_all_over_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Intent hensigt = new Intent(this, SettingsActivity.class);
      startActivity(hensigt);
      return true;
    }
    if (id == R.id.action_om) {
      Intent hensigt = new Intent(this, OmActivity.class);
      startActivity(hensigt);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onStart() {
    super.onStart();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Beer all Over", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.lundwall.beerallover/http/host/path")
    );
    AppIndex.AppIndexApi.start(client, viewAction);
  }

  @Override
  public void onStop() {
    super.onStop();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Beer all Over", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("https://beerallover.com"), // ML De ønsker null , virker ikke vi ikke har en BeerAlloverMain page ??
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.lundwall.beerallover/http/host/path")
    );
    AppIndex.AppIndexApi.end(client, viewAction);
    client.disconnect();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      fragment.setArguments(args);
      return fragment;
    }

/**
 * Utils til PlaceholderFragment
 */

    /**
     * Lukker og viser advarsel hvis der ikke er internet og heller intet lagret land
     */
    private void tjekForLuk() {
      if (!isConnected() && !dataiDb()) {
        try {
          (new AlertDialog.Builder(this.getActivity()))
                  .setTitle("Info")
                  .setMessage(BAPcontext.getString(R.string.NoInternet_exiting))
                  .setCancelable(false)
                  .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      System.exit(0);
                      dialog.cancel();
                    }
                  }).show();

        } catch (Exception e) {
          if (BuildConfig.DEBUG)
            Toast.makeText(BAPcontext, "FEJL 2 tjekForLuk " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    }

    /**
     * Util til lavListe,
     * Sætter grund-ting op for linier i listen
     */
    private void lavListe_txtSetup(TextView t, String s, int w,TableRow.LayoutParams p) {
      t.setText(s);
      t.setWidth(w);
      t.setTextColor(ContextCompat.getColor(BAPcontext, R.color.colorListeTekst));
      t.setPadding(0,10,1,5);
      t.setLayoutParams(p);
    }

    /**
     * Util til LavReviews "Send review"
     * Escaper karakterer over 128 og " ", "&", "<" og ">"
     * Returnerer escapet streng
     * !! Tilsyneladende virker &, < og > ikke !!
     *         */
    public static String ML_escapeHTML(String s) {
      StringBuilder out = new StringBuilder(Math.max(16, 4*s.length()));

      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);

        if (c > 127) {
          out.append("%");
          out.append(Integer.toHexString((int) c));
        }
        else
        if (c==' ')
          out.append("%20");
        else
        if (c=='&')
          out.append("&amp;");
        else
        if (c=='<')
          out.append("&le;");
        else
        if (c=='>')
          out.append("&ge;");
        else
          out.append(c);
      }
      return out.toString();
    }

/**
 *  Afsnit:
 *  Utils DB-funktioner
 */

    /**
     * 1) Gemmer land i database
     * 2) Sætter gemtLand til landeId
     * 3) Gemmer data
     */
    private void gemLand() {
      if (isConnected() && landeId!=landIdB) {
        if (glemLand()) {
          // Bruger olListeArray til at hente øl-Id'er i aktuelt land
          ListeObj listObj;

          Toast.makeText(BAPcontext, BAPcontext.getString(R.string.Saving_country), Toast.LENGTH_SHORT).show();
          try {
            int i = 0;
            while (i < olListeArray.size()) {
              listObj = (ListeObj) olListeArray.get(i);
              int _olId = listObj.Id;
              // Nednstående nødvendigt, da JSonmlæseren bruger den globale olId
              olId = _olId;
              if (_olId > 0) {
                // Henter udfra Id
                try {
                  InternetTask asynkTråd = new InternetTask();
                  asynkTråd.execute("ol", rodUrl + olphp + "?OlId=" + _olId).get();
                } catch (Exception e) {
                  olObj = new OlObj(BAPcontext.getString(R.string.beerNotSelected),0.0,"",0.0,"",null,"-");
                  if (BuildConfig.DEBUG)
                    Toast.makeText(BAPcontext, "FEJL 3 Ol " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                dataBase.gemOl(_olId, olObj);
                i++;
              }
            }
          } catch (Exception e) {
            if (BuildConfig.DEBUG)
              Toast.makeText(BAPcontext, "FEJL 4 Ol " + e.getMessage(), Toast.LENGTH_LONG).show();
          }
          olId = -1;
          landIdB = landeId;
          gemData();
        }
      }
      else
      if (!isConnected())
      {
        Toast.makeText(BAPcontext, BAPcontext.getString(R.string.NoInternet), Toast.LENGTH_LONG).show();
      }
    }

    /**
     *  Henter en øl fra land fra internet eller database
     */
    private OlObj hentOl() {
      if (isConnected()) {
        try {
          InternetTask asynkTråd = new InternetTask();
          // Side-effekt: initierer olObj:
          asynkTråd.execute("ol", rodUrl + olphp + "?OlId=" + olId).get();
        } catch (Exception e) {
          if (BuildConfig.DEBUG)
            Toast.makeText(BAPcontext, "FEJL 5 url "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
      else {
        // Laver dataBase hvis nødvendigt
        if (dataBase == null)
          dataBase = new db(BAPcontext);
        if (landIdB == landeId)
          olObj = dataBase.hentOl(olId);
        else
          olObj = null;
      }
      return olObj;
    }

    /**
     *  Henter olListeArray fra det land, der ligger i databasen fra database
     */
    private ArrayList hentListe() {
      if (isConnected()) {
        try {
          InternetTask asynkTråd = new InternetTask();
          // Side-effekt: initierer olListeArray:
          asynkTråd.execute("liste", rodUrl + listephp + "?LandeId=" + landeId).get();
        } catch (Exception e) {
          if (BuildConfig.DEBUG)
            Toast.makeText(BAPcontext, "FEJL 6 url "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
      else {
        if (aktueltLandGemtiDb()) {
          // Laver dataBase hvis nødvendigt
          if (dataBase == null)
            dataBase = new db(BAPcontext);
          olListeArray=dataBase.hentListe();
        }
        else
          return null;
      }
      return olListeArray;
    }

    private static void hentReview(int olId){
      if (isConnected() && (olId>0))
        try {
          InternetTask asynkTråd = new InternetTask();
          asynkTråd.execute("review", rodUrl + reviewphp + "?OlId=" + olId + "&uid=" + uid).get();
        } catch (Exception e) {
          if (BuildConfig.DEBUG)
            Toast.makeText(BAPcontext, "FEJL 7 Hentreview "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
      else {
        // Måske: olObj = null;
        brugerReview = null;
      }
    }

    /**
     * 1) Sletter land i databdase
     * 2) Sætter landIdB til -1
     */
    private boolean glemLand() {
      landIdB = -1;
      gemData();
      // Laver dataBase hvis nødvendigt
      if (dataBase == null)
        dataBase=new db(BAPcontext);
      // Og tømmer den ...
      return dataBase.Toem();
    }

/**
 *  Afsnit:
 *  VIEW-funktioner
 */

    /**
     * Laver reView udfra olId
     */
    private void lavReview(final View rootView) {
      ArrayList<ImageButton> knap = new ArrayList<ImageButton>();
      // ML Tilføjer de fire knapper i rotview til et array til iteration
      knap.add(0,(ImageButton) rootView.findViewById(R.id.s1));
      knap.add(1,(ImageButton) rootView.findViewById(R.id.s2));
      knap.add(2,(ImageButton) rootView.findViewById(R.id.s3));
      knap.add(3,(ImageButton) rootView.findViewById(R.id.s4));
      knap.add(4,(ImageButton) rootView.findViewById(R.id.s5));
      // ML Henter og initierer øvrige elementer i rootview
      TextView n = rootView.findViewById(R.id.olnavn);
      EditText c = rootView.findViewById(R.id.olcomment);
      Button send = rootView.findViewById(R.id.sendreview);

      if (isConnected() && (olId>0)) {
        send.setText(getString(R.string.SendReview));
        send.setTextColor(ContextCompat.getColor(BAPcontext, R.color.colorMorkTekst));
        send.setEnabled(true);
      }
      else {
        if (!isConnected())
          send.setText(getString(R.string.beerOffline));
        else
          send.setText(getString(R.string.beerNotSelected));
        send.setTextColor(Color.GRAY);
        send.setEnabled(false);
      }

      // Henter denne brugers rewiew
      hentReview(olId);

      if (olObj != null)
        n.setText(olObj.Navn);
      else
      if (landIdB==landeId)
        n.setText(getString(R.string.beerOffline));
      else
        n.setText(getString(R.string.beerNotSelected));

      if (brugerReview != null)
      {
        lokalrating= brugerReview.Rating;
        ((TextView) rootView.findViewById(R.id.olnavnlabel)).setText(R.string.ChangeReview);
        for (int i = 0; i <= 4; i++) {
          if (lokalrating <= i)
            knap.get(i).setBackgroundResource(R.drawable.star0);
          else
            knap.get(i).setBackgroundResource(R.drawable.star1);
        }
        c.setText(brugerReview.Comment);
        c.setTextColor(Color.BLACK);
      }
      else {
        lokalrating = 0;
        ((TextView) rootView.findViewById(R.id.olnavnlabel)).setText(R.string.MakeReview);
        c.setText(getString(R.string.YourComment));
        c.setTextColor(Color.GRAY);
      }

      // Rettes: Nej, det er IKKE denne kommentar, det er en fra Rating, og kun hvis denne bruger HAR rated denne øl før
      (knap.get(0)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          lokalrating=1;
          (rootView.findViewById(R.id.s1)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s2)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s3)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s4)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s5)).setBackgroundResource(R.drawable.star0);
        }
      });

      (knap.get(1)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          lokalrating=2;
          (rootView.findViewById(R.id.s1)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s2)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s3)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s4)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s5)).setBackgroundResource(R.drawable.star0);
        }
      });

      (knap.get(2)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          lokalrating=3;
          (rootView.findViewById(R.id.s1)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s2)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s3)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s4)).setBackgroundResource(R.drawable.star0);
          (rootView.findViewById(R.id.s5)).setBackgroundResource(R.drawable.star0);
        }
      });

      (knap.get(3)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          lokalrating=4;
          (rootView.findViewById(R.id.s1)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s2)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s3)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s4)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s5)).setBackgroundResource(R.drawable.star0);
        }
      });

      (knap.get(4)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          lokalrating=5;
          (rootView.findViewById(R.id.s1)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s2)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s3)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s4)).setBackgroundResource(R.drawable.star1);
          (rootView.findViewById(R.id.s5)).setBackgroundResource(R.drawable.star1);
        }
      });

      c.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
          EditText e=(EditText) v;
          InputMethodManager keyboard = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
          if (hasFocus){
            if (e.getText().toString().equals(getString(R.string.YourComment))) {
              e.setText("");
              e.setTextColor(Color.BLACK);
            }
            keyboard.showSoftInput(e, InputMethodManager.SHOW_IMPLICIT);
          }
          else
            keyboard.hideSoftInputFromWindow(e.getWindowToken(), 0);
        }
      });

      send.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          InternetTask asynkTråd = new InternetTask();
          EditText c = reView.findViewById(R.id.olcomment);
          String url;

          if (brugerReview==null)
            brugerReview = new ReviewObj(lokalrating,c.getText().toString());
          else {
            brugerReview.Comment=c.getText().toString();
            brugerReview.Rating=lokalrating;
          }

          url  = rodUrl + gemratingphp;
          url += "?OlId="    + olId;
          url += "&Rating="  + brugerReview.Rating;
          url += "&UserId="  +uid;
          url += "&Comment=" +ML_escapeHTML(brugerReview.Comment);
          try{
            if (asynkTråd.execute("url", url).get().equals("OK")) {
              ((Button) v).setText(getString(R.string.ReviewSent));
              ((Button) v).setTextColor(Color.GRAY);
              v.setEnabled(false);
            }
          } catch (Exception e) {
            if (BuildConfig.DEBUG)
              Toast.makeText(BAPcontext, "FEJL 8 url "+e.getMessage(), Toast.LENGTH_LONG).show();
          }
        }
      });
    }

    /**
     * Forbereder OlViev udfra olId
     */
    private void lavOl(final View rootView) {
      TextView navn = rootView.findViewById(R.id.navn);
      TextView bryggeri = rootView.findViewById(R.id.bryggeri);
      TextView pct = rootView.findViewById(R.id.pct);
      TextView rating = rootView.findViewById(R.id.rating);
      ImageView billede = rootView.findViewById(R.id.billede);

      try {
        if (olId>0) {
          if (nyOl())
            hentOl();
          if (olObj != null) {
            navn.setText(olObj.Navn);
            bryggeri.setText(olObj.Bryggeri);
            pct.setText(String.format("%.1f", olObj.Pct));
            rating.setText(String.format("%.1f", olObj.Rating));

            if (olObj.Billede != null) {
              billede.setImageBitmap(olObj.Billede);
            } else
              billede.setImageResource(R.drawable.tomol);
          }
        }
        else
        {
          navn.setText(getString(R.string.beerNotSelected));
          bryggeri.setText("-");
          pct.setText("-");
          rating.setText("-");
          billede.setImageResource(R.drawable.tomol);
          if (reView != null)
            lavReview(reView);
        }

      } catch (Exception e) {
        if (BuildConfig.DEBUG)
          Toast.makeText(BAPcontext, "FEJL 9 Ol " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
    }

    /**
     * Forbereder olliste udfra landeId
     */
    private void lavListe(final View rootView) {
      ListeObj listeObj;
      try {
        if (!isConnected() && aktueltLandGemtiDb())
          landeId = landIdB;

        if (nytLand()) {
          gemLandeId  = landeId;
          olListeArray = hentListe();
          olId = -1;
          olObj = null;
        }
        TextView l = rootView.findViewById(R.id.landelabel);
        l.setText(landenavn); // Viser hvilket land listen er fra

        final TableLayout Tabel = rootView.findViewById(R.id.tabel);
        TextView[][] Felt = new TextView[150][3];
        Tabel.removeViews(1, Tabel.getChildCount() - 1);
        if (olListeArray==null || olListeArray.size() == 0) {
          olId = -1;
          if (olView != null)
            lavOl(olView);
          TableRow Raekke = new TableRow(this.getContext());
          Tabel.addView(Raekke);
          TextView hjaelp = new TextView(this.getContext());
          // ML Sådan henter man en tekst string streng ressource - i @Strigs\NoBeersHere
          hjaelp.setText(getString(R.string.NoBeersHere));
          TableRow.LayoutParams llp= new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
          llp.gravity= Gravity.RIGHT;
          hjaelp.setLayoutParams(llp);
          hjaelp.setWidth(175);
          hjaelp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
          Raekke.addView(hjaelp);
          Raekke.setTag(-1);
        }
        else {
          TableRow.LayoutParams llp0 = (TableRow.LayoutParams) rootView.findViewById(R.id.NameLabel).getLayoutParams();
          TableRow.LayoutParams llp1 = (TableRow.LayoutParams) rootView.findViewById(R.id.StyleLabel).getLayoutParams();
          TableRow.LayoutParams llp2 = (TableRow.LayoutParams) rootView.findViewById(R.id.RatingLabel).getLayoutParams();

          for (int i = 0; i < olListeArray.size(); i++) {
            listeObj = (ListeObj) olListeArray.get(i);
            TableRow Raekke = new TableRow(this.getContext());
            final int sort = Tabel.getSolidColor();
            // ML Marker valgt øl
            if (listeObj.Id == olId)
              Raekke.setBackgroundColor(Color.LTGRAY);
            Raekke.setTag(listeObj.Id);

            Raekke.setClickable(true);  // Gør rækkerne valgbare
            Raekke.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                // Kommer for sent:
                // Toast.makeText(getContext(), R.string.WaitShort, Toast.LENGTH_SHORT).show();
                for (int i = 0; i <= olListeArray.size(); i++)
                  Tabel.getChildAt(i).setBackgroundColor(sort);
                v.setBackgroundColor(Color.LTGRAY);
                olId = (int) v.getTag();
                if (nyOl()) {
                  lavOl(olView);
                  lavReview(reView);
                  mViewPager.setCurrentItem(2, true);
                  gemolId=olId;
                }
              }
            });
            Felt[i][0] = new TextView(this.getContext());
            lavListe_txtSetup(Felt[i][0],listeObj.Navn,150,llp0);
            Raekke.addView(Felt[i][0]);

            Felt[i][1] = new TextView(this.getContext());
            lavListe_txtSetup(Felt[i][1],listeObj.OlType,70,llp1);
            Raekke.addView(Felt[i][1]);

            Felt[i][2] = new TextView(this.getContext());
            lavListe_txtSetup(Felt[i][2],listeObj.Rating.toString(),50,llp2);
            Felt[i][2].setGravity(Gravity.RIGHT);
            Raekke.addView(Felt[i][2]);

            Tabel.addView(Raekke);
          }
          // Tilføj tom bund-entry:
          TableRow Raekke = new TableRow(this.getContext());
          TextView SlutFelt = new TextView(this.getContext());
          lavListe_txtSetup(SlutFelt,"",150,llp0);
          Raekke.addView(SlutFelt);

          lavListe_txtSetup(SlutFelt,"",150,llp0);
          Raekke.addView(SlutFelt);

          lavListe_txtSetup(SlutFelt,"",150,llp0);
          Raekke.addView(SlutFelt);

          Tabel.addView(Raekke);
          // Tilføj tom bund-entry slut
        }
      }
      catch (Exception e) {
        if (BuildConfig.DEBUG)
          Toast.makeText(BAPcontext, "FEJL 10 liste " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
    }

    /**
     * Forbereder landeDrop
     */
    private void lavLandeDrop(final View rootView) {
      // Nu hentes fra intern liste, så denne tråd bruges ikke
      //   InternetTask asynkTråd = new InternetTask();
      List<String> spinnerArray = new ArrayList<String>();
      LandObj landObj;
      TextView l = rootView.findViewById(R.id.section_label);
      // GPS disabled: Button GPSKnap = (Button) rootView.findViewById(R.id.GPSknap);
      Button GemLandKnap = rootView.findViewById(R.id.GemLandKnap);
      l.setText(getString(R.string.DropdownTitle)); //l.setText(getString(R.string.label_titel, getArguments().getInt(ARG_SECTION_NUMBER)));

      // LandeArray skal være initieret inden vi kan køre hent-data (GPSen skal kunne vælge fre dropdown)
      landeArray = PutLande.lav();
      // Henter fra bao.ini og evt land fra GPS og Dyt
      hentDataOgInit();
      boolean forbundet = isConnected();
      // Ser, at vi ikke har mistet forbindelsen
      tjekForLuk();
      // Hvis vi har et land i databasen og er offline, er det KUN det der vises
      int valgt = -1;
      try {
        spinnerArray.clear();
        for (int i = 0; i < landeArray.size(); i++) {
          landObj = (LandObj) landeArray.get(i);

          if ((landIdB == landObj.Id) && !forbundet)
          { // Vi er ikke forbundne, og vælge det land der er lagret
            valgt = 0;
            spinnerArray.add(landObj.Navn);
          }
          else if (forbundet)
          { // Ellers vises alle lande, og vi vælger det, der blev set sidst eller pr GPS
            spinnerArray.add(landObj.Navn);
            if (landObj.Navn.equals(landenavn)) {
              valgt = i;
            }
          }
        }
      } catch (Exception e) {
        if (BuildConfig.DEBUG)
          Toast.makeText(BAPcontext, "FEJL 11 land " + e.getMessage(), Toast.LENGTH_LONG).show();
      }

      // GPS Disablet
            /*
            if (setup_fragps)
                GPSKnap.setVisibility(View.INVISIBLE);
            else {
                GPSKnap.setVisibility(View.VISIBLE);
                GPSKnap.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LandObj landObj;
                        landenavn = findland();
                        int valgt = 0;
                        for (int i = 0; i < landeArray.size(); i++) {
                            landObj = (LandObj) landeArray.get(i);
                            if (landObj.Navn.equals(landenavn))
                                valgt = i;
                        }
                        ((Spinner) findView.findViewById(R.id.spinner)).setSelection(valgt);
                    }
                });
            }
             */

      // Testformål: klik på ergemtlabel tømmer database
      TextView ergemtlabel = rootView.findViewById(R.id.ergemt);
      ergemtlabel.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          glemLand();
          visOmErGemt(findView);
        }
      });
      // Testformål slut

      visOmErGemt(rootView);

      GemLandKnap.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          gemLand();
          visOmErGemt(findView);
        }
      });

      ArrayAdapter<String> adapter;
      adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      Spinner sItems = rootView.findViewById(R.id.spinner);
      sItems.setAdapter(adapter);
      if (valgt>=0)
        sItems.setSelection(valgt);

      sItems.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
          LandObj landObj;
          landObj = (LandObj) landeArray.get(position);
          if (isConnected() && (landeId != landObj.Id)) {
            // Vi har valgt et nyt land OG vi er forbundne
            landeId = landObj.Id;
            landenavn=landObj.Navn;
            lavListe(listView);
            /* Her kunne vi enabler&disable: findView.setEnabled(true);listView.setEnabled(true);olView.setEnabled(false);reView.setEnabled(false);Slut */
            gemData();
            // Nulstiller Øl og Review
            olId = -1;
            // Tømmer øl-listen
            lavOl(olView);
            // Giver mulighed for at gemme det ny land:
            visOmErGemt(findView);
            // Vi går til liste-siden
            mViewPager.setCurrentItem(1, true);
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
          // your code here
        }
      });
    }

    /**
     * Danner de aktuelle views udfra menuId
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = null;
      Integer menuId = getArguments().getInt(ARG_SECTION_NUMBER);

      switch (menuId) {
        case (1): {
          rootView = inflater.inflate(R.layout.find, container, false);
          lavLandeDrop(rootView);
          findView =rootView;
          return rootView;
        }
        case (2): {
          rootView = inflater.inflate(R.layout.list, container, false);
          lavListe(rootView);
          listView=rootView;
          return rootView;
        }
        case (3): {
          if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            rootView = inflater.inflate(R.layout.infolodret, container, false);
          else
            rootView = inflater.inflate(R.layout.infovandret, container, false);
          lavOl(rootView);
          olView=rootView;
          return rootView;
        }
        case (4): {
          if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            rootView = inflater.inflate(R.layout.reviewlodret, container, false);
          else
            rootView = inflater.inflate(R.layout.reviewvandret, container, false);
          lavReview(rootView);
          reView=rootView;
          return rootView;
        }
      }
      return null;
    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
      return 4;
    }

    /**
     * Genererer tekst udfra posistionen i menu
     * Ikke vigtig længere
     * @return Tekster, der kan puttes et eller andet sted (oprindeligt i section_label)
     */
    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return "Find";
        case 1:
          return "List";
        case 2:
          return "Info";
        case 3:
          return "Your review";
      }
      return null;
    }
  }
}