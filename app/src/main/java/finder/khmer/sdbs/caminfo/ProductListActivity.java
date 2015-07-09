package finder.khmer.sdbs.caminfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import finder.khmer.sdbs.caminfo.adapter.GlobalValue;
import finder.khmer.sdbs.caminfo.adapter.Helper;
import finder.khmer.sdbs.caminfo.company_list.LazyImageLoadAdapter;
import finder.khmer.sdbs.caminfo.adapter.JSONParser;

/**
 * Created by hort on 6/17/2015.
 */
public class ProductListActivity extends Activity {

    ListView list;
    LazyImageLoadAdapter adapter;

    private JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;

    Helper helper = new Helper();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        list=(ListView)findViewById(R.id.list);



        String newString;
        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                newString = null;
            }else{
                newString = extras.getString("type_of_food_id");
            }
        }else{
            newString = (String) savedInstanceState.getSerializable("type_of_food_id");
        }
      //  Toast.makeText(getApplicationContext(), newString, Toast.LENGTH_LONG).show();




      //  new GettingList().execute("");
        new GettingList(newString).execute("");
    }

    @Override
    public void onDestroy()
    {
        // Remove adapter refference from list
        list.setAdapter(null);
        super.onDestroy();
    }

    public View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {

            //Refresh cache directory downloaded images
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };


    public void onItemClick(int mPosition)
    {
        //Intent intent = new Intent(ProductListActivity.this, ProductDetail.class);
        //intent.putExtra("id", productId[mPosition]);
        //intent.putExtra("name", companyName[mPosition]);
       // startActivity(intent);
        //Toast.makeText(getBaseContext(), "show me", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
        intent.putExtra("product_id", productId[mPosition]);
        startActivity(intent);

    }

    // Image urls used in LazyImageLoadAdapter.java file

    private String[] companyName;
    private String[] priceRange;
    private String[] foodType;
    private String[] address;
    private String[] imagePath;
    private int[] productId;

    class GettingList extends AsyncTask<String, String, String> {

        public GettingList(String type_of_food_id) {
            Toast.makeText(getApplicationContext(), type_of_food_id, Toast.LENGTH_LONG).show();
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(ProductListActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Saving product
         */
        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(GlobalValue.SECURITY_CODE, GlobalValue.SECURITY_CODE_VALUE));
            params.add(new BasicNameValuePair(GlobalValue.REQUEST_CMD, "get_product_list"));

            // sending modified data through http request
            // Notice that update product url accepts POST method


            // check json success tag
            try {
                // Toast.makeText(getApplicationContext(), "do in background method", Toast.LENGTH_LONG).show();

                JSONObject json = jsonParser.makeHttpRequest(GlobalValue.REQUEST_URL + "product_list.php",
                        "POST", params);
                int success = json.getInt("success");

                String image[] = null;
                if (success == 1) {

                    JSONArray arr = json.getJSONArray("product_list");


                    companyName = new String[arr.length()];
                    priceRange = new String[arr.length()];
                    foodType = new String[arr.length()];
                    imagePath = new String[arr.length()];
                    address = new String[arr.length()];
                    productId = new int[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject r = arr.getJSONObject(i);
                        companyName[i] = r.getString("company_name");
                        priceRange[i] = r.getString("price_rang");
                        foodType[i] = r.getString("food_type");
                        imagePath[i] = r.getString("images");
                        address[i] = r.getString("address");
                        productId[i] = r.getInt("id");
                    }




                } else {
                    // failed to update product
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
                        // Create custom adapter for listview
                        try{

                            adapter=new LazyImageLoadAdapter(ProductListActivity.this, companyName, priceRange, foodType, imagePath, address);

                            //Set adapter to listview
                            list.setAdapter(adapter);



                        }catch(Exception e){
                            Toast.makeText(ProductListActivity.this, e.getMessage(), Toast.LENGTH_LONG);

                        }


                    } else {
                        helper.dialog(getBaseContext(), "Message", "No Internet connection");
                    }

                }
            });
            // dismiss the dialog once product uupdated
            pDialog.dismiss();

        }
    }
}