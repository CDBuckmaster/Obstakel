package com.deco3800;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.location.LocationManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.*;
import android.util.Log;

public class MainActivity extends FragmentActivity {

  private GoogleMap mMap;
	private Marker exLocation;
	private LocationManager locationManager;
	private LocationListener listener;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        
    }
    
    protected void onStart(){
    	super.onStart();
    	
    	locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        listener = new LocationListener() {
        
        	private Location currentBest = null;
            @Override
            public void onLocationChanged(Location location) {
                // A new location update is received.  Do something useful with it.  In this case,
                // we're sending the update to a handler which then updates the UI with the new
                // location.
            	if(isBetterLocation(location, currentBest))
            	{
            		currentBest = location;
            	}
            	double accuracy = 0.0005;
            	if(currentBest.getLatitude() <= exLocation.getPosition().latitude + accuracy && currentBest.getLatitude() >= exLocation.getPosition().latitude - accuracy &&
            			currentBest.getLongitude() <= exLocation.getPosition().longitude + accuracy && currentBest.getLongitude() >= exLocation.getPosition().longitude - accuracy)
            	{
            		exLocation.setTitle("Ready");
            	}
            	else
            	{
            		exLocation.setTitle("Away");
            	}
                }

    		@Override
    		public void onProviderDisabled(String provider) {
    			// TODO Auto-generated method stub
    			
    		}

    		@Override
    		public void onProviderEnabled(String provider) {
    			// TODO Auto-generated method stub
    			
    		}

    		@Override
    		public void onStatusChanged(String provider, int status, Bundle extras) {
    			// TODO Auto-generated method stub
    			
    		}
        };
        
        List<String> providers = locationManager.getAllProviders();
        for(String s : providers)
        {
        	Log.e(s, "derp");
        	locationManager.requestLocationUpdates(s, 100, 0, listener);
        }
        
        
    }
    
    protected void onStop()
    {
    	super.onStop();
    	locationManager.removeUpdates(listener);
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        	mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        	exLocation = mMap.addMarker(new MarkerOptions()
        			.position(new LatLng(-27.46368,152.99762))
        			.title("Other"));
        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-27.46368,152.99762), 15));

        }
        else
        {
        	
        }
    }
    
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    	int TWO_MINUTES = 1000 * 60 * 2;

    	if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    

}

