package com.damy.nongyao;

import android.graphics.drawable.Drawable;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class OverlayContainer extends ItemizedOverlay<OverlayItem> {

	public OverlayContainer(Drawable mark, MapView mapView){
		super(mark,mapView);
	}

	protected boolean onTap(int index) {
		return true;
	}

	public boolean onTap(GeoPoint pt, MapView mapView){
		super.onTap(pt,mapView);
		return false;
	}
}
