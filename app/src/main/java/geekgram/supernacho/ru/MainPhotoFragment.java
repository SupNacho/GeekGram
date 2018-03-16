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
import android.widget.Toast;

import java.io.IOException;

import static geekgram.supernacho.ru.MainActivity.CAMERA_CAPTURE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout frameLayout;
    private Fragment allPhotoFragment;
    private Fragment dbFragment;
    private Fragment netFragment;
    private FloatingActionButton fab;
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
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_photo, container, false);

        initFragments();
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        frameLayout = view.findViewById(R.id.containerFragment);
        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_all);
        changeFragment(allPhotoFragment, FragmentTags.ALL_PHOTO);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       try {
                                           ((MainActivity) getContext()).dispatchTakepictureIntent(CAMERA_CAPTURE);
                                       } catch (IOException e){
                                           Toast.makeText(getContext(), "File not found!", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               }
        );
    }

    private void initFragments() {
        allPhotoFragment = AllPhotoFragment.newInstance();
        dbFragment = DbFragment.newInstance(null, null);
        netFragment = NetFragment.newInstance(null, null);
    }

}
