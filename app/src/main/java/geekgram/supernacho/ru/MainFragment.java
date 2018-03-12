package geekgram.supernacho.ru;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.adapter.RecyclerViewAdapter;
import geekgram.supernacho.ru.model.PhotoModel;


public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    List<PhotoModel> photos;

    public MainFragment() {
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initPhotosArray();
        initUI(view);
        return view;
    }

    private void initPhotosArray() {
        photos = new ArrayList<>();
        boolean isFav;
        for (int i = 0; i < 3; i++){
            if (i%3 == 0){
                isFav = true;
            } else {
                isFav = false;
            }
            photos.add(new PhotoModel(isFav, null));
        }
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.main_fragment_recycler_view);
        if (recyclerView != null){
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(photos);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.add(new PhotoModel(false, null));
                Snackbar.make(view, "Add photo pressed", Snackbar.LENGTH_LONG)
                        .setAction("ADD", null).show();
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
