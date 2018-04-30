package geekgram.supernacho.ru;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import geekgram.supernacho.ru.adapter.PhotoFromNetRecyclerViewAdapter;
import geekgram.supernacho.ru.model.api.ApiConst;
import geekgram.supernacho.ru.model.api.ApiService;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.presenters.PhotosFromNetPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotosFromNetFragment extends MvpAppCompatFragment implements PhotosFromNetFragmentView {

    @BindView(R.id.fl_web_view_container_net_fragment)
    FrameLayout frameLayoutWebView;
    @BindView(R.id.iv_user_net_fragment)
    ImageView ivUserAvatar;
    @BindView(R.id.tv_username_net_fragment)
    TextView tvUserName;
    @BindView(R.id.rv_net_fragment)
    RecyclerView recyclerView;
    @BindView(R.id.srl_net_fragment)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectPresenter
    PhotosFromNetPresenter presenter;
    @Inject
    IImageLoader<ImageView> imageLoader;

    private PhotoFromNetRecyclerViewAdapter adapter;
    private Unbinder unbinder;
    ApiService apiService;


    public PhotosFromNetFragment() {
    }

    public static PhotosFromNetFragment newInstance() {
        return new PhotosFromNetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.getInstance().getAppComponent().inject(this);
        apiService = new Retrofit.Builder()
                .baseUrl(ApiConst.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @ProvidePresenter
    public PhotosFromNetPresenter providePresenter() {
        PhotosFromNetPresenter presenter = new PhotosFromNetPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;
    }

    @Override
    public void initUI() {
        swipeRefreshLayout.setOnRefreshListener(this::loadDataFromNet);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryDarkColorYB));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new PhotoFromNetRecyclerViewAdapter(presenter);
        App.getInstance().getAppComponent().inject(adapter);
        recyclerView.setAdapter(adapter);
        loadDataFromNet();
    }

    private void loadDataFromNet() {
        presenter.loadUserData(App.requestToken[0]);
        presenter.loadUserRecent(App.requestToken[0]);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateRecyclerViewAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startViewPhoto(int pos, String uri, boolean isFavorite) {
        Intent viewIntent = new Intent(getActivity(), FullscreenPhotoActivity.class);
        viewIntent.putExtra(AllPhotoFragment.IMG_POS, pos);
        viewIntent.putExtra(AllPhotoFragment.IMG_URI, uri);
        viewIntent.putExtra(AllPhotoFragment.IS_FAVORITE, isFavorite);
        startActivity(viewIntent);
    }

    @Override
    public void loadUserData(String avatarUri, String fullName) {
        imageLoader.loadInto(avatarUri, ivUserAvatar);
        tvUserName.setText(fullName);
    }

    @Override
    public void onHttpException(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);
    }
}
