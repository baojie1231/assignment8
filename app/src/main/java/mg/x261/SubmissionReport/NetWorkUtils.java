package mg.x261.SubmissionReport;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class NetWorkUtils {

    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isNetworkConnected;

    public NetWorkUtils(Context context) {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                isNetworkConnected = true;
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                isNetworkConnected = false;
            }
        };

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        cm.registerNetworkCallback(request, networkCallback);

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        isNetworkConnected = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public void unregisterNetworkCallback(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.unregisterNetworkCallback(networkCallback);
    }

}