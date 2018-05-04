package geekgram.supernacho.ru.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.presenters.FullScreenPresenter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static geekgram.supernacho.ru.view.AllPhotoFragment.IMG_POS;
import static geekgram.supernacho.ru.view.AllPhotoFragment.IMG_URI;
import static geekgram.supernacho.ru.view.AllPhotoFragment.IS_FAVORITE;

public class FullscreenPhotoActivity extends MvpAppCompatActivity implements FullScreenView{
    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private int pos;

    @BindView(R.id.fullscreen_content) View mContentView;

    @InjectPresenter
    FullScreenPresenter presenter;

    @Inject
    IImageLoader<ImageView> imageLoader;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @BindView(R.id.fullscreen_content_controls)
    View mControlsView;
    @BindView(R.id.view_fav_button)
    ImageButton favButton;

    private boolean isFavorite;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        App.getInstance().getAppComponent().inject(this);
        setContentView(R.layout.activity_fullscreen_photo);
        ButterKnife.bind(this);
        mVisible = true;
        initUI();
    }

    @ProvidePresenter
    public FullScreenPresenter providePresenter(){
        FullScreenPresenter presenter = new FullScreenPresenter();
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        mContentView.setOnClickListener(view -> toggle());
        favButton.setOnTouchListener(mDelayHideTouchListener);
        Intent intent = getIntent();
        String stringUri = intent.getStringExtra(IMG_URI);
        isFavorite = intent.getBooleanExtra(IS_FAVORITE, false);
        pos = intent.getIntExtra(IMG_POS, -1);
        Uri imgUri = Uri.parse(stringUri);
        ImageView imgView = (ImageView) mContentView;
        imageLoader.loadInto(stringUri.startsWith("htt")? stringUri : imgUri.getPath(), imgView);
        setFavButtonStateImg();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @OnClick(R.id.view_fav_button)
    public void onClick(View view) {
                isFavorite = !isFavorite;
                setFavButtonStateImg();
                presenter.favoriteIsChanged(pos);
    }

    private void setFavButtonStateImg() {
        if (isFavorite) {
            favButton.setImageResource(R.drawable.ic_favorites_on);
        } else {
            favButton.setImageResource(R.drawable.ic_favorites_off);
        }
    }
}
