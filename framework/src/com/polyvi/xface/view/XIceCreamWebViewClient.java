package com.polyvi.xface.view;

import java.io.IOException;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class XIceCreamWebViewClient extends XWebViewClient {

	  private static final String TAG = "IceCreamCordovaWebViewClient";

	    public XIceCreamWebViewClient(CordovaInterface cordova) {
	        super(cordova);
	    }
	    
	    public XIceCreamWebViewClient(CordovaInterface cordova, CordovaWebView view) {
	        super(cordova, view);
	    }

	    @Override
	    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
	        try {
	            // Check the against the white-list.
	            if ((url.startsWith("http:") || url.startsWith("https:")) && !Config.isUrlWhiteListed(url)) {
	                LOG.w(TAG, "URL blocked by whitelist: " + url);
	                // Results in a 404.
	                return new WebResourceResponse("text/plain", "UTF-8", null);
	            }

	            CordovaResourceApi resourceApi = appView.getResourceApi();
	            Uri origUri = Uri.parse(url);
	            // Allow plugins to intercept WebView requests.
	            Uri remappedUri = resourceApi.remapUri(origUri);
	            
	            if (!origUri.equals(remappedUri) || needsSpecialsInAssetUrlFix(origUri)) {
	                OpenForReadResult result = resourceApi.openForRead(remappedUri, true);
	                return new WebResourceResponse(result.mimeType, "UTF-8", result.inputStream);
	            }
	            // If we don't need to special-case the request, let the browser load it.
	            return null;
	        } catch (IOException e) {
	            LOG.e("IceCreamCordovaWebViewClient", "Error occurred while loading a file.", e);
	            // Results in a 404.
	            return new WebResourceResponse("text/plain", "UTF-8", null);
	        }
	    }

	    private static boolean needsSpecialsInAssetUrlFix(Uri uri) {
	        if (CordovaResourceApi.getUriType(uri) != CordovaResourceApi.URI_TYPE_ASSET) {
	            return false;
	        }
	        if (uri.getQuery() != null || uri.getFragment() != null) {
	            return true;
	        }
	        
	        if (!uri.toString().contains("%")) {
	            return false;
	        }

	        switch(android.os.Build.VERSION.SDK_INT){
	            case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH:
	            case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
	                return true;
	        }
	        return false;
	    }
}
