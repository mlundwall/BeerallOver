/**
 * En asynktron tråd
 * Gjort brugervenlig ved at man kan kande Internettet vha doInBackground() og første parametre:
 * land, liste, ol, review, url
 * Created by mluc on 23-03-2016.
 */
package com.lundwall.beerallover;

import android.os.AsyncTask;
import java.io.IOException;
import static com.lundwall.beerallover.CommonData.*;

    public class InternetTask extends AsyncTask<String, Integer, String> {

        /**
         * Kalder tråden "InternetTask" i baggrunden i forbindelse med Internetopgaver
         * TODO: beskriv denne process nøjere
         * @param 0 String land, liste, ol, review, url
         * @param 1 String url der skal kaldes.
         * Created by mluc on 23-03-2016.
         */
        @Override
        protected String doInBackground(String... params) {
        JsonLaeser dataLaeser = new JsonLaeser();
        String r = "";

            try {
                switch (params[0]) {
                    case ("land") : {
                        landeArray = dataLaeser.laesLande(params[1]);
                        break;
                    }
                    case ("liste") : {
                        olListeArray = dataLaeser.laesListe(params[1]);
                        break;
                    }
                    case ("ol") : {
                        olObj = dataLaeser.laesOl(params[1]);
                        break;
                    }
                    case ("review") :{
                        brugerReview = dataLaeser.laesReview(params[1]);
                        break;
                    }
                    case ("url") :{
                        r = dataLaeser.koerUrl(params[1]);
                        break;
                    }
                    default:
                        return("Fejl");

                }
            }
            catch (IOException e) {
                return("Fejl "+e.getMessage());
            }
            if (r==null || r=="")
                return "OK";
            else
                return(r);
        }

        /**
         * Ikke implementeret
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Ikke implementeret
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }