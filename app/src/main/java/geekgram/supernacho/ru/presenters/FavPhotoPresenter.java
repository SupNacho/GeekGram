package geekgram.supernacho.ru.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.FavFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class FavPhotoPresenter extends MvpPresenter<FavFragmentView> implements IFragmentPresenter{
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private IRepository repository;
    private FlowableSubscriber<List<PhotoModel>> photoObserver;
    private Scheduler uiScheduler;
    private Subscription subscription;

    public FavPhotoPresenter(Scheduler scheduler) {
        this.repository = Repository.getInstance();
        this.uiScheduler = scheduler;
        this.photos = new ArrayList<>();
        this.favPhotos = new ArrayList<>();

    }

    @Override
    public void attachView(FavFragmentView view) {
        super.attachView(view);
        Timber
                .tag("++")
                .d("FP AttachView");
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repository.getStartData();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber
                .tag("++")
                .d("FirstAttach FP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
        photoObserver = new FlowableSubscriber<List<PhotoModel>>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
                Timber
                        .tag("++")
                        .d("Fav Presenter on Subscribe");
                repository.getStartData();
            }

            @Override
            public void onNext(List<PhotoModel> photoModels) {
                photos.clear();
                photos.addAll(photoModels);
                favoriteIsChanged();
                getViewState().updateRecyclerViewAdapter();
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                Timber
                        .tag("++")
                        .d(t.getMessage(), "on Error: %s");
            }

            @Override
            public void onComplete() {
                Timber
                        .tag("++")
                        .d("on Complete");
                getViewState().updateRecyclerViewAdapter();
            }
        };
        repository
                .getObservablePhotos()
                .subscribeOn(Schedulers.computation())
                .observeOn(uiScheduler)
                .subscribe(photoObserver);
//        getViewState().updateRecyclerViewAdapter();
        repository.getStartData();
    }



    @Override
    public void onDestroy() {
        Timber
                .tag("++")
                .d("FP Presenter Destroyed");
        super.onDestroy();
        subscription.cancel();
    }

    @Override
    public void viewPhoto(int pos) {
        PhotoModel pm;
        if (favPhotos != null && favPhotos.size() > pos) {
            if ((pm = favPhotos.get(pos)) != null && pm.getPhotoSrc() != null) {
                int tmpPos = photos.indexOf(pm);
                getViewState().startViewPhoto(tmpPos, pm.getPhotoSrc(), pm.isFavorite());
            }
        }
    }

    @Override
    public void deleteDialog(int pos) {
        getViewState().deletePhoto(pos);
    }

    @Override
    public void deletePhoto(int pos) {
        if (photos != null && photos.size() > pos) {
            photoTmp = favPhotos.get(pos);
            tempPos = photos.indexOf(photoTmp);
            repository.remove(tempPos);
        }
    }

    @Override
    public void undoDeletion() {
        repository.addPhoto(tempPos, photoTmp);
        favoriteIsChanged();
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public List<PhotoModel> getFavPhotos() {
        return favPhotos;
    }

    @Override
    public void favoriteIsChanged() {
        favPhotos.clear();
        for (PhotoModel photo : photos) {
            if (photo.isFavorite()) favPhotos.add(photo);
        }
    }

    @Override
    public void setFavorite(PhotoModel pm) {
        pm.setFavorite(!pm.isFavorite());
        if (pm.isFavorite()) favPhotos.add(pm);
        else favPhotos.remove(pm);
        repository.favoriteIsChanged();
    }
}
