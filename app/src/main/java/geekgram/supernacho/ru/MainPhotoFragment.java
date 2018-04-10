package geekgram.supernacho.ru;


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

import static geekgram.supernacho.ru.MainActivity.CAMERA_CAPTURE;


public class MainPhotoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

    public static MainPhotoFragment newInstance(String param1, String param2) {
        MainPhotoFragment fragment = new MainPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        dbFragment = PhotosFromDbFragment.newInstance(null, null);
        netFragment = PhotosFromNetFragment.newInstance(null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
