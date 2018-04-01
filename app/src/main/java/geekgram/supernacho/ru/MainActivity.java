package geekgram.supernacho.ru;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import geekgram.supernacho.ru.adapter.PhotoFragmentsAdapter;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.presenters.MainPresenter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends MvpAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AllPhotoFragment.OnFragmentInteractionListener,
        MainView{

    public static final int CAMERA_CAPTURE = 1;
    public static final int READ_EXT_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE = 2;

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
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;

    @InjectPresenter
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setTheme(new AppSharedPreferences(this).getSavedTheme());
        initView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initPhotosArrays() {
        photos = new ArrayList<>();
        favPhotos = new ArrayList<>();
        boolean isFav;
        for (int i = 0; i < 3; i++) {
            isFav = (i % 3) == 0;
            photos.add(new PhotoModel(isFav, null));
        }
        for (PhotoModel photo : photos) {
            if (photo.isFavorite()) favPhotos.add(photo);
        }
    }

    private void initTabLayout() {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.setTabTextColors(R.color.secondaryTextColor, R.color.primaryTextColor);
        tabLayout.getTabAt(0).select();
    }

    private void initViewPager() {
        viewPager.setAdapter(photoFragmentsPageAdapter);
    }

    private void initPageAdapter() {
        photoFragmentsPageAdapter = new PhotoFragmentsAdapter(getSupportFragmentManager());
        photoFragmentsPageAdapter.addFragment(mainFragment, "Main");
        photoFragmentsPageAdapter.addFragment(favoriteFragment, "Favorites");
    }

    private void initFragment() {
        mainFragment = MainPhotoFragment.newInstance(null, null);
        favoriteFragment = FavoritesFragment.newInstance(null, null);
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
        initPhotosArrays();
        initNavDrawer();
        initFragment();
        initPageAdapter();
        initViewPager();
        initTabLayout();
    }

    public void dispatchTakePictureIntent(int actionCode) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = presenter.createPhotoFile(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "Camera");
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the File!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        presenter.createPhotoFile(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM), "Camera"));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, actionCode);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(presenter.addPhoto());
                MediaScannerConnection.scanFile(MainActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO: 06.03.2018 implement fragment interaction
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawerSwitch.switchIt(new WeakReference<Context>(this), item.getItemId());
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
                    Log.d("++", "READ GRANTED");
                } else {
                    Log.d("++", "READ NOT GRANTED");
                }
                break;
            case WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("++", "WRITE GRANTED");
                } else {
                    Log.d("++", "WRITE NOT GRANTED");
                }
                break;
            default:
                Log.w("++", "Request code not found");
                break;
        }
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public List<PhotoModel> getFavPhotos() {
        return favPhotos;
    }
}
// TODO: 16.03.2018 выставить размер фото в пикассо