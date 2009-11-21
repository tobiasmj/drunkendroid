package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.Trip;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PreviousTripsActivity extends ListActivity{
   
	private TripRepository _repo;
    private ProgressDialog _progressDialog = null;
    private List<Trip> _trips = null;
    private TripAdapter _adapter;
    private Runnable viewTrips;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previoustrips);
        _repo = new TripRepository(this);
        _trips = new ArrayList<Trip>();
        this._adapter = new TripAdapter(this, R.layout.previoustriprow, _trips);
        setListAdapter(this._adapter);
       
        viewTrips = new Runnable(){
            public void run() {
                getPreviousTrips();
            }
        };
        Thread thread =  new Thread(null, viewTrips, "PreviousTripsThread");
        thread.start();
        _progressDialog = ProgressDialog.show(PreviousTripsActivity.this,    
              "Please wait...", "Retrieving list of trips ...", true);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if(_trips.size() > 0) {
			
		}
	}
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		_repo.closeRepository();		
	}

	@Override
	protected void onPause() {
		super.onPause();
		//_repo.closeRepository();
	}

	@Override
	protected void onResume() {
		super.onResume();
		_repo = new TripRepository(this);
	}

	private Runnable returnRes = new Runnable() {
        public void run() {
            if(_trips != null && _trips.size() > 0){
                _adapter.notifyDataSetChanged();
                for(int i=0;i<_trips.size();i++)
                	_adapter.add(_trips.get(i));
            }
            _progressDialog.dismiss();
            _adapter.notifyDataSetChanged();
        }
    };
    
    private void getPreviousTrips(){
          try{
              _trips = _repo.getAllTrips();
              
              Log.i("ARRAY", ""+ _trips.size());
            } catch (Exception e) {
              Log.e("BACKGROUND_PROC", e.getMessage());
            }
            runOnUiThread(returnRes);
        }
    private class TripAdapter extends ArrayAdapter<Trip> {

        private List<Trip> items;

        public TripAdapter(Context context, int textViewResourceId, List<Trip> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.previoustriprow, null);
                }
                Trip t = items.get(position);
                if (t != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                        if (tt != null) {
                              tt.setText("Date: "+ new Date(t.getStartDate().getTimeInMillis()).toLocaleString());                            }
                        if(bt != null){
                              bt.setText("Events: "+ _repo.getEventCount(t));
                        }
                }
                return v;
        }
        
}
}
