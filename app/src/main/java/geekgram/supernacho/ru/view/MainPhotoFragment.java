package geekgram.supernacho.ru.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import geekgram.supernacho.ru.R;

import static geekgram.supernacho.ru.view.MainActivity.CAMERA_CAPTURE;


public class MainPhotoFragment extends Fragment {

    private Fragment allPhotoFragment;
    private Fragment dbFragment;
    private Fragment netFragment;

    @BindView(R.id.containerFragment)
    FrameLayout frameLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private Unbinder unbinder;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_all:
                            changeFragment(allPhotoFragment, FragmentTags.ALL_PHOTO);
                            return true;
                        case R.id.navigation_db:
                            changeFragment(dbFragment, FragmentTags.DB_PHOTO);
                            return true;
                        case R.id.navigation_net:
                            changeFragment(netFragment, FragmentTags.NET_PHOTO);
                            return true;
                    }
                    return false;
                }
            };

    private void changeFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFragment, fragment, tag);
        transaction.commit();
    }

    public MainPhotoFragment() {
    }

    public static MainPhotoFragment newInstance() {
        return new MainPhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_photo, container, false);
        unbinder = ButterKnife.bind(this, view);
        initFragments();
        initUI();
        return view;
    }

    private void initUI() {
        navigation.setOnNavigationItemSelectedListener(onNavigationSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_all);
        fab.setOnClickListener(view -> {
            if (getContext() != null) {
                ((MainActivity) getContext()).dispatchTakePictureIntent(CAMERA_CAPTURE);
            }
        });
    }

    private void initFragments() {
        allPhotoFragment = AllPhotoFragment.newInstance();
        dbFragment = PhotosFromDbFragment.newInstance();
        netFragment = PhotosFromNetFragment.newInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
