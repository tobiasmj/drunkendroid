package itu.dd.client.ui.map;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedEventOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> _overlays = new ArrayList<OverlayItem>();
	
	public ItemizedEventOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(OverlayItem overlay) {
	    _overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return _overlays.get(i);
	}

	@Override
	public int size() {
		return _overlays.size();
	}

}
