package geekgram.supernacho.ru.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.BuildConfig;
import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.view.adapter.PhotoFragmentsAdapter;
import geekgram.supernacho.ru.presenters.MainPresenter;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends MvpAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView{

    public static final int CAMERA_CAPTURE = 1;
    public static final int READ_EXT_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE = 2;

    @Inject
    App app;

    @BindView(R.id.vp_container)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private PhotoFragmentsAdapter photoFragmentsPageAdapter;
    private Fragment mainFragment;
    private Fragment favoriteFragment;

    @InjectPresenter
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getAppComponent().inject(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_name))
                .setFontAttrId(R.attr.fontPath)
                .build());
        setTheme(new AppSharedPreferences(this).getSavedTheme());
        initView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initTabLayout() {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.setTabTextColors(R.color.secondaryTextColor, R.color.primaryTextColor);
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    private void initViewPager() {
        viewPager.setAdapter(photoFragmentsPageAdapter);
    }

    private void initPageAdapter() {
        photoFragmentsPageAdapter = new PhotoFragmentsAdapter(getSupportFragmentManager());
        photoFragmentsPageAdapter.addFragment(mainFragment, getResources().getString(R.string.fragment_main_title));
        photoFragmentsPageAdapter.addFragment(favoriteFragment, getResources().getString(R.string.fragment_fav_title));
    }

    private void initFragment() {
        mainFragment = MainPhotoFragment.newInstance();
        favoriteFragment = FavoritesFragment.newInstance();
    }

    private void initNavDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main);


    }

    private void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestReadPermission();
        requestWritePermission();
        initNavDrawer();
        initFragment();
        initPageAdapter();
        initViewPager();
        initTabLayout();
    }

    @ProvidePresenter
    public MainPresenter providerMainPresenter(){
        MainPresenter presenter = new MainPresenter();
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    public void dispatchTakePictureIntent(int actionCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            final File[] photoFile = {null};
                presenter.createPhotoFile(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), getResources().getString(R.string.photo_child_dir))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<File>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Timber
                                        .tag("++")
                                        .d("PhotoFile on subscribe");
                            }

                            @Override
                            public void onSuccess(File file) {
                                photoFile[0] = file;
                                Timber
                                        .tag("++")
                                        .d("PhotoFile = %s", photoFile[0].getName());
                                if (photoFile[0] != null) {
                                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                            BuildConfig.APPLICATION_ID + ".provider",
                                            photoFile[0]);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, actionCode);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                errorToast("Error occurred while creating the File!" + e);
                            }
                        });
        }
    }

    private void errorToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(presenter.addPhoto());
                MediaScannerConnection.scanFile(MainActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        (path, uri) -> {
                        });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawerSwitch.switchIt(new WeakReference<>(this), item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestReadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXT_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void requestWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXT_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("READ GRANTED");
                } else {
                    Timber.d("READ NOT GRANTED");
                }
                break;
            case WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("WRITE GRANTED");
                } else {
                    Timber.d("WRITE NOT GRANTED");
                }
                break;
            default:
                Timber.w("Request code not found");
                break;
        }
    }
}