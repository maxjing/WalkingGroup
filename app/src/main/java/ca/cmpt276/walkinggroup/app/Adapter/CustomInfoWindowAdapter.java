package ca.cmpt276.walkinggroup.app.Adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ca.cmpt276.walkinggroup.app.R;

/**
 * customize Info window to show marker's information
 * can be hidden by information button on GoogleMap Activity
 */


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){
        Log.i("CustomInfoWindowAdapter", "getLatlng:" + marker.getPosition());
        Log.i("CustomInfoWindowAdapter", "getName:" + marker.getSnippet());
        String title = marker.getTitle();
        TextView textView = view.findViewById(R.id.title);

        if (!title.equals("")){
            textView.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView textSnippet = view.findViewById(R.id.snippet);

        if (!snippet.equals("")){
            textSnippet.setText(snippet);
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }
}
