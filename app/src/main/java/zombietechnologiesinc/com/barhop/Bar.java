package zombietechnologiesinc.com.barhop;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 9/22/2016.
 */

public class Bar {
    private String barName;
    private int barCount;
    private int barCap;
    private String barAddress;
    private String barPhotoURI;
    private String userId;

    public String getBarEvent() {
        return barEvent;
    }

    public void setBarEvent(String barEvent) {
        this.barEvent = barEvent;
    }

    private String barEvent;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    private String placeId;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getBarPhotoURI() {
        return barPhotoURI;
    }

    public void setBarPhotoURI(String barPhotoURI) {
        this.barPhotoURI = barPhotoURI;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public int getBarCount() {
        return barCount;
    }

    public void setBarCount(int barCount) {
        this.barCount = barCount;
    }

    public int getBarCap() {
        return barCap;
    }

    public void setBarCap(int barCap) {
        this.barCap = barCap;
    }

    public String getBarAddress() {
        return barAddress;
    }

    public void setBarAddress(String barAddress) {
        this.barAddress = barAddress;
    }

    public Bar() {

    }

    public Bar(String barName, int barCount, int barCap, String barAddress, String barPhotoURI, double latitude,
               double longitude, String userId, String placeId, String barEvent) {
        this.barCap = barCap;
        this.barCount = barCount;
        this.barName = barName;
        this.barAddress = barAddress;
        this.barPhotoURI = barPhotoURI;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.placeId = placeId;
        this.barEvent = barEvent;

    }

    public Bar(Map<String,Object> map){
        this((String)map.get("barName"),(int)map.get("barCount"), (int)map.get("barCap"),
                (String)map.get("barAddress"),(String)map.get("barPhotoURI"),(double)map.get("barLatitude"),
                (double)map.get("barLongitude"), (String)map.get("userId"), (String)map.get("placeId"),
                (String)map.get("barEvent"));
    }

    public Map<String, Object> toMap(){
        Map<String, Object> barMap = new HashMap<String, Object>();
        barMap.put("barName", this.barName);
        barMap.put("barCount", this.barCount);
        barMap.put("barCap", this.barCap);
        barMap.put("barAddress", this.barAddress);
        barMap.put("barPhotoURI", this.barPhotoURI);
        barMap.put("barLatitude", this.latitude);
        barMap.put("barLongitude", this.longitude);
        barMap.put("userId", this.userId);
        barMap.put("barEvent", this.barEvent);
        return barMap;
    }
}


