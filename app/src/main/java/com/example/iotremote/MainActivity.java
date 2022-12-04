package com.example.iotremote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

//import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.iotremote.api.ApiService;
import com.example.iotremote.model.Currency;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(
                getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        setContentView(R.layout.load_map);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
//        GeoPoint startPoint = new GeoPoint(10.86815, 106.79931);
        IMapController mapController = map.getController();
        mapController.setZoom(16.0);
//============== Start call map bounds
        ApiService.apiService.loadMap().enqueue(new Callback<Currency>() {
            @Override
            public void onResponse(Call<Currency> call, Response<Currency> response) {
                Toast.makeText(MainActivity.this, "Call API Thanh Cong!", Toast.LENGTH_LONG).show();
                Currency currency = response.body();
                List<Float> centerList = currency.getOptions().getDefaults().getBounds();
                //Log.d("API CALL", centerList.get(1)+""); -> Dòng này để kiểm tra API call về
                Float kd = centerList.get(0);
                Float vd = centerList.get(1);
                GeoPoint startPoint = new GeoPoint(vd, kd);
                mapController.setCenter(startPoint);
            }
            @Override
            public void onFailure(Call<Currency> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Call API Map That Bai!", Toast.LENGTH_LONG).show();
                Log.d("API CALL", t.getMessage().toString());
            }
        });
//==============>end call map bounds
//============== Start call asset

//==============>end call asset

        ArrayList<OverlayItem> items = new ArrayList<>();
//        OverlayItem home = new OverlayItem("Rallo's office", "my office", new GeoPoint(10.87304, 106.80524));
//        Drawable m = home.getMarker(0);
//        items.add(home);
        items.add(new OverlayItem("Asset vi du", "Mo ta ngan", new GeoPoint(10.87304, 106.79931)));
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                showBottomSheetDialog();
                //Toast.makeText(MainActivity.this, "Bottom Sheet Thanh Cong!", Toast.LENGTH_LONG).show();
                return true;
            }
            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });
        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
    }


    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }
    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);
        bottomSheetDialog.show();
    }

}