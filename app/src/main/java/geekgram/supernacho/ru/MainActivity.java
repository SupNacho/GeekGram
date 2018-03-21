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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AllPhotoFragment.OnFragmentInteractionListener {

    public static final int CAMERA_CAPTURE = 1;
    public static final int READ_EXT_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int WRITE_EX_STORAGE_PERMISSION_REQUEST_CODE = 2;

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
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabLayout.setTabTextColors(R.color.secondaryTextColor, R.color.primaryTextColor);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        if ((mainFragment.getChildFragmentManager().findFragmentByTag(FragmentTags.ALL_PHOTO)) != null) {
                            ((AllPhotoFragment) mainFragment.getChildFragmentManager().findFragmentByTag(FragmentTags.ALL_PHOTO))
                                    .getAdapter().notifyDataSetChanged();
                        }
                        break;
                    case 1:
                        if ((favoriteFragment) != null) {
                            ((FavoritesFragment) favoriteFragment).getAdapter().notifyDataSetChanged();
                        }
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "No such tab", Toast.LENGTH_LONG).show();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).select();
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(photoFragmentsPageAdapter);
        Log.d("++//", "All ph: " + R.layout.fragment_allphoto + "\n" +
                "DB ph: " + R.layout.fragment_db + "\n Net ph: " + R.layout.fragment_net + "\n Main: " +
        R.layout.fragment_main_photo);
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

    private void initView() {
        setContentView(R.layout.activity_main);
        requestReadPermission();
        requestWritePermission();
        initPhotosArrays();
        initNavDrawer();
        initFragment();
        initPageAdapter();
        initViewPager();
        initTabLayout();
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
        Log.d("++//", "Storage: " + storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("++//", "Img: " + image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Log.d("++//", "Result: " + currentPhotoPath);
            if (currentPhotoPath != null) {
                Uri imageUri = Uri.parse(currentPhotoPath);
                Fragment allPhotoFragment = mainFragment.getChildFragmentManager().findFragmentByTag(FragmentTags.ALL_PHOTO);
                ((AllPhotoFragment) allPhotoFragment).addPhoto(imageUri);
                MediaScannerConnection.scanFile(MainActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
// TODO: 16.03.2018 разобраться с работой фото на АсусZ5 (как вариант блокировать поворот)