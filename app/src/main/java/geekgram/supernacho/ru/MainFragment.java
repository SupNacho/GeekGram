package geekgram.supernacho.ru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.adapter.RecyclerViewAdapter;
import geekgram.supernacho.ru.model.PhotoModel;


public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private List<PhotoModel> photos;
    private RecyclerViewAdapter adapter;
    private FloatingActionButton fab;

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
        adapter = new RecyclerViewAdapter(photos, new WeakReference<>(this));
        recyclerView.setAdapter(adapter);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photos.add(new PhotoModel(false, null));
                Snackbar.make(view, "Add photo pressed", Snackbar.LENGTH_LONG)
                        .setAction("ADD", null).show();
            }
        });
    }

    public void deletePhoto(final int pos){
        final PhotoModel[] tempPhoto = new PhotoModel[1];
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert
                .setMessage(R.string.alert_delete_msg)
                .setCancelable(true)
                .setNegativeButton(R.string.alert_negative_txt, null)
                .setPositiveButton(R.string.alert_positive_txt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tempPhoto[0] = photos.remove(pos);
                        adapter.notifyDataSetChanged();
                        Snackbar.make(fab, "Photo deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                photos.add(pos, tempPhoto[0]);
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                })
                .show();

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
