package finder.khmer.sdbs.caminfo.company_list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import finder.khmer.sdbs.caminfo.ProductListActivity;
import finder.khmer.sdbs.caminfo.R;
import finder.khmer.sdbs.caminfo.image_loader.ImageLoader;

//Adapter class extends with BaseAdapter and implements with OnClickListener 
public class LazyImageLoadAdapter extends BaseAdapter implements OnClickListener{
    
    private Activity activity;

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    private String[] companyName;
    private String[] priceRange;
    private String[] foodType;
    private String[] address;
    private String[] imageName;
    private String[] locationName;
    
    public LazyImageLoadAdapter(Activity a, String[] cname, String[] cprice, String[] cf, String[] ci, String[] cl) {
        activity = a;

        companyName = cname;
        priceRange = cprice;
        foodType = cf;
        imageName = ci;
        locationName = cl;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return companyName.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView text;
        public TextView text1;
        public TextView textWide;
        public ImageView image;
        public TextView address;
 
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View vi=convertView;
        ViewHolder holder;
         
        if(convertView==null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listview_row, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.text1=(TextView)vi.findViewById(R.id.text1);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.address = (TextView) vi.findViewById(R.id.address);

           /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else 
            holder=(ViewHolder)vi.getTag();
        
        
        holder.text.setText(companyName[position]);
        holder.text1.setText("price: " +priceRange[position]);
        holder.address.setText("address: " +locationName[position]);
        ImageView image = holder.image;
        
        //DisplayImage function from ImageLoader Class
        imageLoader.DisplayImage(imageName[position], image);
        
        /******** Set Item Click Listner for LayoutInflater for each row ***********/
        vi.setOnClickListener(new OnItemClickListener(position));
        return vi;
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        
       OnItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public void onClick(View arg0) {
        	ProductListActivity sct = (ProductListActivity)activity;
        	sct.onItemClick(mPosition);
        }               
    }   
}