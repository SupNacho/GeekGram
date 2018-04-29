package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.PhotosFromNetFragmentView;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.api.ApiConst;
import geekgram.supernacho.ru.model.entity.recent.Datum;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

@InjectViewState
public class PhotosFromNetPresenter extends MvpPresenter<PhotosFromNetFragmentView> implements INetFragmentPresenter {
    private List<PhotoModel> photos;
    private Scheduler uiScheduler;
    private Disposable disposeRecent;
    private Disposable disposeUser;

    @Inject
    NetRepository repository;

    public PhotosFromNetPresenter(Scheduler scheduler) {
        this.photos = new ArrayList<>();
        this.uiScheduler = scheduler;
    }

    @Override
    public void attachView(PhotosFromNetFragmentView view) {
        super.attachView(view);
    }

    @Override
    protected void onFirstViewAttach() {
        Timber
                .tag("++")
                .d("FirstAttach NetP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
    }

    @Override
    public void loadUserData(String token) {
        disposeUser = repository.getUser(token)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(u -> getViewState()
                        .loadUserData(u.getData().getProfilePicture(), u.getData().getFullName()));
    }

    @Override
    public void loadUserRecent(String token) {
        disposeRecent = repository.getUserRecentData(token)
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .doOnError(err -> {
                    if (err instanceof HttpException) {
                        HttpException exception = (HttpException) err;
                        switch (exception.code()) {
                            case ApiConst.AUTH_BAD_REQUEST:
                                System.out.printf("CODE %s\n", exception.response().toString());
                                getViewState().onHttpException("Bad request");
                                break;
                            case ApiConst.AUTH_REQUIRED:
                                getViewState().onHttpException(exception.message());
                                break;
                            default:
                                getViewState().onHttpException(exception.message());
                                break;
                        }
                    }
                })
                .subscribe(ur -> {
                        photos.clear();
                        for (Datum datum : ur.getData()) {
                            photos.add(new PhotoModel(false, datum.getImages().getStandardResolution().getUrl()));
                        }
                        getViewState().updateRecyclerViewAdapter();
                    });
    }

    @Override
    public void onDestroy() {
        Timber
                .tag("++")
                .d("NetP Presenter Destroyed");
        super.onDestroy();
        disposeUser.dispose();
        disposeRecent.dispose();
    }

    @Override
    public void viewPhoto(int pos) {
        PhotoModel pm;
        if (photos != null && photos.size() > pos) {
            if ((pm = photos.get(pos)) != null && pm.getPhotoSrc() != null) {
                getViewState().startViewPhoto(pos, pm.getPhotoSrc(), pm.isFavorite());
            } else {
                Timber.d("---- Fuckup with viewing photo: %d %s %s", pos, photos.get(pos).getPhotoSrc(), photos.get(pos).isFavorite());
            }
        }
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public List<PhotoModel> getFavPhotos() {
        return new ArrayList<>();
    }

    @Override
    public void favoriteIsChanged() {

    }

    @Override
    public void setFavorite(PhotoModel pm) {
        pm.setFavorite(!pm.isFavorite());
    }
}
