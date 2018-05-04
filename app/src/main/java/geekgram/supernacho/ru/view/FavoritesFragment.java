package geekgram.supernacho.ru.view;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.view.adapter.FavPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.presenters.FavPhotoPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static geekgram.supernacho.ru.view.AllPhotoFragment.IMG_POS;
import static geekgram.supernacho.ru.view.AllPhotoFragment.IMG_URI;
import static geekgram.supernacho.ru.view.AllPhotoFragment.IS_FAVORITE;


public class FavoritesFragment extends MvpAppCompatFragment implements FavFragmentView{

    @BindView(R.id.favorites_fragment_recycler_view) RecyclerView recyclerView;
    private Unbinder unbinder;
    private FavPhotoRecyclerViewAdapter adapter;

    @InjectPresenter
    FavPhotoPresenter presenter;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        App.getInstance().getAppComponent().inject(adapter);
        recyclerView.setAdapter(adapter);
    }

    public FavPhotoRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public void deletePhoto(final int pos){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert
                .setMessage(R.string.alert_delete_msg)
                .setCancelable(true)
                .setNegativeButton(R.string.alert_negative_txt, null)
                .setPositiveButton(R.string.alert_positive_txt, (dialogInterface, i) -> {
                    presenter.deletePhoto(pos);
                    Snackbar.make(recyclerView, R.string.snkbar_photo_delete_text, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snkbar_photo_delete_bnt, view -> presenter.undoDeletion()).show();
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
