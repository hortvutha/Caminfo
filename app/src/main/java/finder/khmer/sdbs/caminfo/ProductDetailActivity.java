package finder.khmer.sdbs.caminfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import finder.khmer.sdbs.caminfo.adapter.GlobalValue;
import finder.khmer.sdbs.caminfo.adapter.Helper;
import finder.khmer.sdbs.caminfo.adapter.JSONParser;
import finder.khmer.sdbs.caminfo.image_loader.ImageLoader;


public class ProductDetailActivity extends ActionBarActivity {

    private Helper helper = new Helper();
    private JSONParser jsonParser = new JSONParser();

    private Button map;
    private Button call;
    private Button share;
    private Button btnMorePhoto;
    private Gallery gallery;

    private TextView txtCompanyName;
    private TextView txtPriceRang;
    private TextView txtVisited;
    private TextView txtWorkingHoure;
    private TextView txtTel;
    private TextView txtAddress;
    private TextView txtWebsite;
    private TextView txtReasonLike;
    private TextView txtDescription;
    private TextView txtBestPlace;

    private String strCompanyName;
    private String strPriceRang;
    private String strVisited;
    private String strWorkingHoure;
    private String strTel;
    private String strAddress;
    private String strWebsite;
    private String strReasonLike;
    private String strDescription;
    private String strBestPlace;
    private double mapLat;
    private double mapLng;
    private int mapZoom;
    String[] companyImages;

    private String companyId;

    private ProgressDialog pDialog;



    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle  extras = getIntent().getExtras();
        companyId = extras.getInt("product_id")+"";

        // Note that Gallery view is deprecated in Android 4.1---
        gallery = (Gallery) findViewById(R.id.gallery1);

        txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        txtPriceRang = (TextView) findViewById(R.id.txtPriceRang);
        txtVisited = (TextView) findViewById(R.id.txtVisited);
        txtWorkingHoure = (TextView) findViewById(R.id.txtWorkingHoure);
        txtTel = (TextView) findViewById(R.id.txtTel);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtWebsite = (TextView) findViewById(R.id.txtWebsite);
        txtReasonLike = (TextView) findViewById(R.id.txtReasonLike);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtBestPlace = (TextView) findViewById(R.id.txtBestPlace);

        new GettingData().execute("");

        // Action footer
        map = (Button) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductDetailMapActivity.class);
                intent.putExtra("x", mapLat);
                intent.putExtra("y", mapLng);
                intent.putExtra("company_name", strCompanyName);
                startActivity(intent);
            }
        });
        call = (Button) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Call", Toast.LENGTH_SHORT).show();
            }
        });
        share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Share", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ViewMorePhoto() {
       // Intent intent = new Intent(this, ViewMorePhotoActivity.class);
       // startActivity(intent);
    }

    public void ActionFooter(){
        Toast.makeText(getApplicationContext(), "Underconstructor", Toast.LENGTH_SHORT).show();
    }

    public void ShowImages(String imagIDs){

        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("img_name", imagIDs);
       startActivity(intent);
    }



    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return companyImages.length;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);



            imageView.setLayoutParams(new Gallery.LayoutParams(300,500));
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setMargins(0, 0, 0, 0);
            //imageView.setLayoutParams(params);

            Picasso.with(ProductDetailActivity.this).load(companyImages[position]).into(imageView);
            imageView.getLayoutParams().height = 500;
            imageView.getLayoutParams().width = 700;
            imageView.setPadding(0, 0, 0, 0);
            imageView.setAdjustViewBounds(true);

            // imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }

    private ImageLoader imageLoader = new ImageLoader(ProductDetailActivity.this);

    class GettingData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ProductDetailActivity.this);
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
            params.add(new BasicNameValuePair("product_id", companyId));

            // check json success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(GlobalValue.REQUEST_URL + "product_detail.php",
                        "POST", params);
                int success = json.getInt("success");

                Log.d("Error", success+"");

                if (success == 1) {

                    JSONObject arr = json.getJSONObject("product_detail");
                    strCompanyName = arr.getString("company_name");

                    strPriceRang = arr.getString("price_rang");
                    strVisited = arr.getString("visited");
                    strWorkingHoure = arr.getString("working_hour");
                    strTel = arr.getString("Tel");
                    strAddress = arr.getString("address");
                    strWebsite = arr.getString("website");
                    strReasonLike = arr.getString("reason_liked");
                    strDescription = arr.getString("description");
                    strBestPlace = arr.getString("best_place");
                    mapLat = arr.getDouble("x");
                    mapLng = arr.getDouble("y");

                    JSONArray jsonImages = arr.getJSONArray("images");
                    companyImages = new String[jsonImages.length()];
                    for(int i=0;i< jsonImages.length();i++){
                        JSONObject jsonImage = jsonImages.getJSONObject(i);
                        companyImages[i] = jsonImage.getString("image");
                    }
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
                    if (helper.isNetworkAvailable(ProductDetailActivity.this)) {
                        try {

                            txtCompanyName.setText(strCompanyName);
                            txtPriceRang.setText(strPriceRang);
                            txtVisited.setText(strVisited);
                            txtWorkingHoure.setText(strWorkingHoure);
                            txtTel.setText(strTel);
                            txtAddress.setText(strAddress);
                            txtWebsite.setText(strWebsite);
                            txtReasonLike.setText(strReasonLike);
                            txtDescription.setText(strDescription);
                            txtBestPlace.setText(strBestPlace);


                            gallery.setAdapter(new ImageAdapter(getApplicationContext()));
                            if ( companyImages.length > 2){
                                gallery.setSelection(1);
                            }
                            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    Toast.makeText(getBaseContext(), id + " selected", Toast.LENGTH_SHORT).show();
                                    // display the images selected
                                    ShowImages(companyImages[position]);

                                }
                            });
                        } catch (Exception e) {
                            helper.showToast(ProductDetailActivity.this, e.getMessage());
                        }
                    } else {
                        helper.dialog(getBaseContext(), "Message", "No Internet connection");
                    }

                    pDialog.dismiss();
                }
            });
        }
    }
}
