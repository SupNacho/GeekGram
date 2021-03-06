package geekgram.supernacho.ru.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.view.adapter.AllPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.presenters.AllPhotoPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class AllPhotoFragment extends MvpAppCompatFragment implements AllPhotoFragmentView {

    public static final String IMG_URI = "img_uri";
    public static final String IMG_POS = "img_pos";
    public static final String IS_FAVORITE = "is_favorite";

    @BindView(R.id.all_photo_fragment_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.srl_all_photo_fragment)
    SwipeRefreshLayout swipeRefreshLayout;

    private Unbinder unbinder;
    private AllPhotoRecyclerViewAdapter adapter;

    @InjectPresenter
    AllPhotoPresenter presenter;

    public AllPhotoFragment() {
    }


    @NonNull
    public static AllPhotoFragment newInstance() {
        return new AllPhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allphoto, container, false);
        unbinder = ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    @ProvidePresenter
    public AllPhotoPresenter providePresenter() {
        AllPhotoPresenter presenter = new AllPhotoPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void initUI() {
        swipeRefreshLayout.setOnRefreshListener(this::updatePhotoCollection);
        if (recyclerView != null) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new AllPhotoRecyclerViewAdapter(presenter);
        App.getInstance().getAppComponent().inject(adapter);
        recyclerView.setAdapter(adapter);

    }

    private void updatePhotoCollection(){
        presenter.updateCollections();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void deletePhoto(final int pos) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert
                .setMessage(R.string.alert_delete_msg)
                .setCancelable(true)
                .setNegativeButton(R.string.alert_negative_txt, null)
                .setPositiveButton(R.string.alert_positive_txt, (dialogInterface, i) -> {
                    presenter.deletePhoto(pos);
                    Snackbar.make(Objects.requireNonNull(Objects.requireNonNull(getParentFragment())
                                    .getView()).findViewById(R.id.fab),
                            R.string.snkbar_photo_delete_text, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snkbar_photo_delete_bnt,
                            view -> presenter.undoDeletion()).show();
                })
                .show();

    }

    public AllPhotoRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
