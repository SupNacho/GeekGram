package geekgram.supernacho.ru;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import geekgram.supernacho.ru.adapter.FavPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.presenters.FavPhotoPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static geekgram.supernacho.ru.AllPhotoFragment.IMG_POS;
import static geekgram.supernacho.ru.AllPhotoFragment.IMG_URI;
import static geekgram.supernacho.ru.AllPhotoFragment.IS_FAVORITE;


public class FavoritesFragment extends MvpAppCompatFragment implements FavFragmentView{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.favorites_fragment_recycler_view) RecyclerView recyclerView;
    private Unbinder unbinder;
    private FavPhotoRecyclerViewAdapter adapter;

    @InjectPresenter
    FavPhotoPresenter presenter;


    public FavoritesFragment() {
        // Required empty public constructor
    }

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
        unbinder = ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    @ProvidePresenter
    public FavPhotoPresenter providePresenter(){
        FavPhotoPresenter presenter = new FavPhotoPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void initUI() {
        if (recyclerView != null){
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new FavPhotoRecyclerViewAdapter(presenter);
        recyclerView.setAdapter(adapter);
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
                        presenter.deletePhoto(pos);
                        Snackbar.make(recyclerView, "Photo deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                presenter.undoDeletion();
                            }
                        }).show();
                    }
                })
                .show();

    }

    @Override
    public void updateRecyclerViewAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startViewPhoto(int pos, String uri, boolean isFavorite) {
        Intent viewIntent = new Intent(getActivity(), FullscreenPhotoActivity.class);
        viewIntent.putExtra(IMG_POS, pos);
        viewIntent.putExtra(IMG_URI, uri);
        viewIntent.putExtra(IS_FAVORITE, isFavorite);
        startActivity(viewIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
