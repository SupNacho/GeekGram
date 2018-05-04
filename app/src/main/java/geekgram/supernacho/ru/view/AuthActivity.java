package geekgram.supernacho.ru;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import geekgram.supernacho.ru.model.api.ApiConst;
import timber.log.Timber;

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.fl_wv_container_auth_activity)
    FrameLayout frameLayout;

    @BindView(R.id.pb_auth_activity)
    ProgressBar progressBar;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        loadDataFromNet();
    }

    private void loadDataFromNet() {
        webView = new WebView(Objects.requireNonNull(getApplicationContext()));
        frameLayout.addView(webView);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.loadUrl("https://api.instagram.com/oauth/authorize/" +
                "?client_id=" + ApiConst.CLIENT_ID +
                "&redirect_uri=" + ApiConst.REDIRECT_URI +
                "&response_type=token");
    }

    @OnClick(R.id.btn_skip_login)
    public void btnSkipAuthClick(){
        frameLayout.removeAllViews();
        webView.destroy();
        App.requestToken[0] = "0000";
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }

    class AuthWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.INVISIBLE);
            Timber.d("onPage FINISHED");
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Timber.d("onPage STARTED");
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Timber.d(url);
            if (url.startsWith(ApiConst.REDIRECT_URI)) {
                Timber.d(url);
                String parts[] = url.split("=");
                App.requestToken[0] = parts[1];
                Timber.d(App.requestToken[0]);
                progressBar.setVisibility(View.INVISIBLE);
                frameLayout.removeAllViews();
                webView.destroy();
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                AuthActivity.this.finish();
                return true;
            }
            return false;
        }
    }
}
