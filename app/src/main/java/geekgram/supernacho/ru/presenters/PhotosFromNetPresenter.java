package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.PhotosFromDbFragmentView;
import geekgram.supernacho.ru.PhotosFromNetFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.entity.recent.Datum;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class PhotosFromNetPresenter extends MvpPresenter<PhotosFromNetFragmentView> implements INetFragmentPresenter {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private FlowableSubscriber<List<PhotoModel>> photoObserver;
    private Scheduler uiScheduler;
    private Subscription subscription;
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
        Timber
                .tag("++")
                .d("NetP AttachView");
//        repository.getStartData();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber
                .tag("++")
                .d("FirstAttach NetP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
//        photoObserver = new FlowableSubscriber<List<PhotoModel>>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//            subscription = s;
//            subscription.request(1);
//            Timber
//                    .tag("++")
//                    .d("NetPPresenter on Subscribe");
//        }
//
//        @Override
//        public void onNext(List<PhotoModel> photoModels) {
//            Timber.tag("++").d("Size %d", photoModels.size());
//            photos.clear();
//            photos.addAll(photoModels);
//            getViewState().updateRecyclerViewAdapter();
//            subscription.request(1);
//        }
//
//        @Override
//        public void onError(Throwable t) {
//            Timber
//                    .tag("++")
//                    .d(t.getMessage(), "on Error %s");
//        }
//
//        @Override
//        public void onComplete() {
//            Timber
//                    .tag("++")
//                    .d("on Complete");
//            getViewState().updateRecyclerViewAdapter();
//        }
//    };
//        repository
//                .getObservablePhotos()
//                .subscribeOn(Schedulers.computation())
//            .observeOn(uiScheduler)
//                .subscribe(photoObserver);
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
//        subscription.cancel();
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
