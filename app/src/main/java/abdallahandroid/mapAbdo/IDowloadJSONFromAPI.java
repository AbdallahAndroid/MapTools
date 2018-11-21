package abdallahandroid.mapAbdo;

import com.google.android.gms.maps.model.Marker;

public interface IDowloadJSONFromAPI {

    void success_data();
    void faild_quota(Marker mark1, Marker mark2); //"error_message" : "You have exceeded your daily request quota for this API. If you did not set a custom daily request quota, verify your project has an active billing account: http://g.co/dev/maps-no-account",
    void faild_response(); //not complete download
}
