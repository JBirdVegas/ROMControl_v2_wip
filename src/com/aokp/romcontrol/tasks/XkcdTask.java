package com.aokp.romcontrol.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class XkcdTask extends AsyncTask<Void, Void, String> {
    private Context mContext;

    public XkcdTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        BufferedReader in = null;
        try {
            URL url = new URL("http://xkcd.com/info.0.json");
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder(0);
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
            JSONObject object = new JSONObject(builder.toString());
            return object.getString("img");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);
        if (url == null) {
            Toast.makeText(mContext, "Well that failed :(",
                    Toast.LENGTH_SHORT).show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(
                mContext, android.R.style.Theme_Translucent_NoTitleBar);
        WebView webView = new WebView(mContext);
        webView.loadUrl(url);
        final AlertDialog alertDialog = builder.setView(webView)
                .create();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                alertDialog.show();
            }
        });
    }
}