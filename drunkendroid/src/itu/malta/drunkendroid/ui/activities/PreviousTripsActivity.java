package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.IncomingCallEvent;
import itu.malta.drunkendroid.domain.IncomingSMSEvent;
import itu.malta.drunkendroid.domain.OutgoingCallEvent;
import itu.malta.drunkendroid.domain.OutgoingSMSEvent;
import itu.malta.drunkendroid.domain.Trip;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PreviousTripsActivity extends ListActivity {

	private TripRepository _repo;
	private ProgressDialog _progressDialog = null;
	private List<Trip> _trips = new ArrayList<Trip>();
	private TripAdapter _adapter;
	private final int VIEW_TRIP = Menu.FIRST;
	private final int UPLOAD_TRIP = Menu.FIRST + 1;
	private final int DELETE_TRIP = Menu.FIRST + 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previoustrips);
		_repo = new TripRepository(this);
		this._adapter = new TripAdapter(this, R.layout.previoustriprow, _trips);
		setListAdapter(_adapter);
		registerForContextMenu(getListView());
	}

	private void UpdateList() {
		Runnable job = new Runnable() {
			public void run() {
				getPreviousTrips();
			}
		};
		Thread thread = new Thread(null, job, "PreviousTripsThread");
		thread.start();
		_progressDialog = ProgressDialog.show(PreviousTripsActivity.this,
				"Please wait...", "Retrieving list of trips ...", true);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ShowTrip(position);
	}

	private void ShowTrip(int position) {
		if (_trips.size() > 0) {
			Intent i = new Intent("VIEW_TRIP");
			i.putExtra("startTime", _trips.get(position).startDate);
			startActivity(i);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_repo.closeRepository();
	}

	@Override
	protected void onResume() {
		super.onResume();
		UpdateList();
	}

	private Runnable returnRes = new Runnable() {
		public void run() {
			_adapter.clear();
			if (_trips != null && _trips.size() > 0) {
				_adapter.notifyDataSetChanged();
				for (int i = 0; i < _trips.size(); i++)
					_adapter.add(_trips.get(i));
			}
			_progressDialog.dismiss();
			_adapter.notifyDataSetChanged();
		}
	};

	private void getPreviousTrips() {
		try {
			_trips = _repo.getAllTrips();

			Log.i("ARRAY", "" + _trips.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private class TripAdapter extends ArrayAdapter<Trip> {

		private List<Trip> items;

		public TripAdapter(Context context, int textViewResourceId,
				List<Trip> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.previoustriprow, null);
			}
			Trip t = items.get(position);
			if (t != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Date: "
							+ new Date(t.startDate).toLocaleString());
				}
				if (bt != null) {
					bt.setText("Events: " + _repo.getEventCount(t));
				}
			}
			return v;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, VIEW_TRIP, 0, "View trip on map");
		menu.add(0, UPLOAD_TRIP, 0, "Upload trip");
		menu.add(0, DELETE_TRIP, 0, "Delete trip");
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case VIEW_TRIP:
			Log.i(this.getString(R.string.log_tag), "Showing trip on map");
			ShowTrip((int) info.id);
			return true;
		case UPLOAD_TRIP:
			showDialog((int) info.id);
			return true;
		case DELETE_TRIP:
			DeleteTrip((int) info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void DeleteTrip(final int id) {
		Log.i(this.getString(R.string.log_tag), "Deleting trip with id: " + id);
		_progressDialog = ProgressDialog.show(PreviousTripsActivity.this,
				"Please wait...", "Deleting trip...", true);
		Runnable job = new Runnable() {

			public void run() {
				try {
					_repo.deleteTrip(_trips.get(id).startDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
				getPreviousTrips();
			}
		};
		Thread thread = new Thread(null, job, "DeleteTripThread");
		thread.start();
	}

	@Override
	protected Dialog onCreateDialog(final int id) {

		final CharSequence[] items = getResources().getStringArray(
				R.array.possible_upload_types);
		final MultiChoiceListener listener = new MultiChoiceListener();

		return new AlertDialog.Builder(PreviousTripsActivity.this).setTitle(
				R.string.upload_choice).setMultiChoiceItems(items,
				new boolean[] { true, true, true, true }, listener)
				.setPositiveButton("Upload",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								_progressDialog = ProgressDialog.show(
										PreviousTripsActivity.this,
										"Uploading trip", "Please wait..");
								Thread t = new Thread(new Runnable() {
									public void run() {
										_repo
												.uploadTrip(
														(_trips.get(id)).startDate,
														listener._choices);
										_progressDialog.dismiss();	
									}

								});
								t.start();
								// TODO upload implementation here!
								Log.i("DrunkDroid", "UPLOADER!"
										+ listener._choices.toString());
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Dont do anything
							}
						}).create();
	}

	class MultiChoiceListener implements OnMultiChoiceClickListener {

		public List<Class<?>> _possibleChoices = new ArrayList<Class<?>>();
		public Set<Class<?>> _choices = new HashSet<Class<?>>();

		public MultiChoiceListener() {
			_possibleChoices.add(IncomingCallEvent.class);
			_possibleChoices.add(OutgoingCallEvent.class);
			_possibleChoices.add(IncomingSMSEvent.class);
			_possibleChoices.add(OutgoingSMSEvent.class);

			_choices.addAll(_possibleChoices);
		}

		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			if (_choices.contains(_possibleChoices.get(which)) && !isChecked) {
				_choices.remove(_possibleChoices.get(which));
			} else if (!_choices.contains(_possibleChoices.get(which))
					&& isChecked) {
				_choices.add(_possibleChoices.get(which));
			}
		}
	}
}
