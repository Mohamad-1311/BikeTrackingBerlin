package de.htw_berlin.mob_sys.biketrackingberlin.controller;
import com.google.maps.android.PolyUtil;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

public class PolylineUtil {

    public static String encodePolyline(List<GeoPoint> geoPoints) {
        List<LatLng> latLngList = new ArrayList<>();
        for (GeoPoint geoPoint : geoPoints) {
            latLngList.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }
        return PolyUtil.encode(latLngList);
    }

    public static List<GeoPoint> decodePolyline(String encoded) {
        List<LatLng> latLngList = PolyUtil.decode(encoded);
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (LatLng latLng : latLngList) {
            geoPoints.add(new GeoPoint(latLng.latitude, latLng.longitude));
        }
        return geoPoints;
    }
}
