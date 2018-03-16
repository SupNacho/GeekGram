package geekgram.supernacho.ru;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import geekgram.supernacho.ru.adapter.FavPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.adapter.PhotoInterface;
import geekgram.supernacho.ru.adapter.AllPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.model.PhotoModel;

import static geekgram.supernacho.ru.AllPhotoFragment.IMG_URI;
import static geekgram.supernacho.ru.AllPhotoFragment.IS_FAVORITE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements PhotoInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private FavPhotoRecyclerViewAdapter adapter;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initPhotosArray();
        initUI(view);
        return view;
    }
    private void initPhotosArray() {
        photos = ((MainActivity)getContext()).getPhotos();
        favPhotos = ((MainActivity)getContext()).getFavPhotos();
        updateFavList();
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.favorites_fragment_recycler_view);
        if (recyclerView != null){
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new FavPhotoRecyclerViewAdapter(((MainActivity)getContext()).getPhotos(),
                ((MainActivity)getContext()).getFavPhotos(), new WeakReference<PhotoInterface>(this));
        recyclerView.setAdapter(adapter);
    }

    public void viewPhoto(int pos){
        if (favPhotos.get(pos).getPhotoSrc() != null) {
            String imgUri = favPhotos.get(pos).getPhotoSrc().toString();
            Intent viewIntent = new Intent(getActivity(), FullscreenPhotoActivity.class);
            viewIntent.putExtra(IMG_URI, imgUri);
            viewIntent.putExtra(IS_FAVORITE, photos.get(pos).isFavorite());
            startActivity(viewIntent);
        } else {
            Snackbar.make(recyclerView, "No such photo", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void addPhoto(Uri photoUri){
        photos.add(new PhotoModel(false, photoUri));
        Snackbar.make(recyclerView, "Photo added successfully", Snackbar.LENGTH_SHORT).show();
    }

    public FavPhotoRecyclerViewAdapter getAdapter() {
        return adapter;
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
                        PhotoModel pm = favPhotos.get(pos);
                        final int undoPos = photos.indexOf(pm);
                        tempPhoto[0] = photos.get(undoPos);
                        photos.remove(pm);
                        updateFavList();
                        adapter.notifyDataSetChanged();
                        Snackbar.make(recyclerView, "Photo deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                photos.add(undoPos, tempPhoto[0]);
                                updateFavList();
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
                    }
                })
                .show();

    }

    private void updateFavList() {
        favPhotos.clear();
        for (PhotoModel photo : photos) {
            if (photo.isFavorite()) favPhotos.add(photo);
        }
    }

}
