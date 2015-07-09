package finder.khmer.sdbs.caminfo.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hort on 6/26/2015.
 */
public class Example extends Activity {

    private Helper helper = new Helper();
    private JSONParser jsonParser = new JSONParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.main);

        // calling asyn class
        new GettingList().execute("");
    }

    class GettingList extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Saving product
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(GlobalValue.SECURITY_CODE, GlobalValue.SECURITY_CODE_VALUE));
            params.add(new BasicNameValuePair(GlobalValue.REQUEST_CMD, "get_product_list"));

            // check json success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(GlobalValue.REQUEST_URL + "product_list.php",
                        "POST", params);
                int success = json.getInt("success");
                String image[] = null;
                if (success == 1) {

                    JSONArray arr = json.getJSONArray("product_list");

                    // your code here


                } else {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (helper.isNetworkAvailable(getBaseContext())) {
                        try {

                            // your code here

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        helper.dialog(getBaseContext(), "Message", "No Internet connection");
                    }

                }
            });
        }
    }

}
