package geekgram.supernacho.ru;

import android.app.AlertDialog;
import android.content.Context;
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

import java.lang.ref.WeakReference;
import java.util.List;

import geekgram.supernacho.ru.adapter.PhotoInterface;
import geekgram.supernacho.ru.adapter.AllPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.model.PhotoModel;


public class AllPhotoFragment extends Fragment implements PhotoInterface {

    public static final String IMG_URI = "img_uri";
    public static final String IS_FAVORITE = "is_favorite";
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private AllPhotoRecyclerViewAdapter adapter;

    public AllPhotoFragment() {
    }


    public static AllPhotoFragment newInstance() {
        AllPhotoFragment fragment = new AllPhotoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allphoto, container, false);
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
        recyclerView = view.findViewById(R.id.all_photo_fragment_recycler_view);
        if (recyclerView != null){
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new AllPhotoRecyclerViewAdapter(((MainActivity)getContext()).getPhotos(),
                ((MainActivity)getContext()).getFavPhotos(), new WeakReference<PhotoInterface>(this));
        recyclerView.setAdapter(adapter);
    }

    public void viewPhoto(int pos){
        if (photos.get(pos).getPhotoSrc() != null) {
            String imgUri = photos.get(pos).getPhotoSrc().toString();
            Intent viewIntent = new Intent(getActivity(), FullscreenPhotoActivity.class);
            viewIntent.putExtra(IMG_URI, imgUri);
            viewIntent.putExtra(IS_FAVORITE, photos.get(pos).isFavorite());
            startActivity(viewIntent);
        } else {
            Snackbar.make(getParentFragment().getView().findViewById(R.id.fab), "No such photo", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void addPhoto(Uri photoUri){
        photos.add(new PhotoModel(false, photoUri));
        Snackbar.make(getParentFragment().getView().findViewById(R.id.fab), "Photo added successfully", Snackbar.LENGTH_SHORT).show();
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
                        updateFavList();
                        adapter.notifyDataSetChanged();
                        Snackbar.make(getParentFragment().getView().findViewById(R.id.fab), "Photo deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                photos.add(pos, tempPhoto[0]);
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public AllPhotoRecyclerViewAdapter getAdapter() {
        return adapter;
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
