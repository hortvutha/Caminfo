package finder.khmer.sdbs.caminfo;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import finder.khmer.sdbs.caminfo.adapter.Helper;
import finder.khmer.sdbs.caminfo.main.CustomGridViewAdapter;
import finder.khmer.sdbs.caminfo.main.Item;


public class MainActivity extends ActionBarActivity {

    private GridView gridView;
    private ArrayList<Item> gridArray = new ArrayList<Item>();
    private CustomGridViewAdapter customGridAdapter;
    private Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( helper.isNetworkAvailable(getBaseContext())){
            //set grid view item
            Bitmap IndiaIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img3);
            Bitmap ChinaIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img4);
            Bitmap FranceIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img6);
            Bitmap BrazilIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img7);
            Bitmap KhmerIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cat);
            Bitmap KhmerKromIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cat);
            /*Bitmap PortugalIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img13);
            Bitmap CanadaIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img14);
            Bitmap JapanIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img8);
            Bitmap KoreaIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img9);
            Bitmap MexicoIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img10);
            Bitmap RussiaIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img11);
            Bitmap SpainIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img5);
            Bitmap NetherlandsIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.img12);

           */
            gridArray.add(new Item(IndiaIcon,"India"));
            gridArray.add(new Item(ChinaIcon,"China"));
            gridArray.add(new Item(FranceIcon,"France"));
            gridArray.add(new Item(BrazilIcon,"Brazil"));
            gridArray.add(new Item(KhmerIcon,"Khmer"));
            gridArray.add(new Item(KhmerKromIcon,"Khmer Krom"));
           /* gridArray.add(new Item(PortugalIcon,"Portugal"));
            gridArray.add(new Item(CanadaIcon,"Canada"));

            gridArray.add(new Item(JapanIcon,"Japan"));
            gridArray.add(new Item(KoreaIcon,"Korea"));
            gridArray.add(new Item(MexicoIcon,"Mexico"));
            gridArray.add(new Item(RussiaIcon,"Russia"));
            gridArray.add(new Item(SpainIcon,"Spain"));
            gridArray.add(new Item(NetherlandsIcon,"Netherlands"));

            */


            gridView = (GridView) findViewById(R.id.gridView1);
            customGridAdapter = new CustomGridViewAdapter(this, R.layout.activity_main_row, gridArray);
            gridView.setAdapter(customGridAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    Toast.makeText(getApplicationContext(), gridArray.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                    intent.putExtra("type_of_food_id","I will not fail in exam  ");
                    startActivity(intent);

                }
            });

            //Read more: http://www.androidhub4you.com/2013/07/custom-grid-view-example-in-android.html#ixzz3dsQwCxDk
        }else{
            helper.dialog(getBaseContext());
        }

    }

    class GettingList extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            // dismiss the dialog once product uupdated

        }
    }

}
