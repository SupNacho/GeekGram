package geekgram.supernacho.ru;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import geekgram.supernacho.ru.adapter.PhotoFragmentsAdapter;
import geekgram.supernacho.ru.model.PhotoModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AllPhotoFragment.OnFragmentInteractionListener {

    public static final int CAMERA_CAPTURE = 1;

    private String currentPhotoPath;
    private ViewPager viewPager;
    private PhotoFragmentsAdapter photoFragmentsPageAdapter;
    private Fragment mainFragment;
    private Fragment favoriteFragment;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPhotosArrays();
        initNavDrawer();
        initFragment();
        initPageAdapter();
        initViewPager();
        initTabLayout();
        RefWatcher refWatcher = LeakCanary.install(this.getApplication());
        refWatcher.watch(this);
    }

    private void initPhotosArrays() {
        photos = new ArrayList<>();
        favPhotos = new ArrayList<>();
        boolean isFav;
        for (int i = 0; i < 15; i++){
            if (i%3 == 0){
                isFav = true;
            } else {
                isFav = false;
            }
            photos.add(new PhotoModel(isFav, null));
        }
        for (PhotoModel photo : photos) {
            if (photo.isFavorite()) favPhotos.add(photo);
        }
    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.setTabTextColors(R.color.secondaryTextColor, R.color.primaryTextColor);
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.container);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main);


    }

    public void dispatchTakepictureIntent(int actionCode) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the File!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, actionCode);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(currentPhotoPath);
            Fragment allPhotoFragment = mainFragment.getChildFragmentManager().findFragmentByTag("00001");
            ((AllPhotoFragment) allPhotoFragment).addPhoto(imageUri);
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public List<PhotoModel> getFavPhotos() {
        return favPhotos;
    }
}
