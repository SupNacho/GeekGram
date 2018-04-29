package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.FavFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.RepoEvents;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class FavPhotoPresenter extends MvpPresenter<FavFragmentView> implements IFragmentPresenter {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    @Inject
    IRepository repository;
    private Observer<RepoEvents> photoObserver;
    private Scheduler uiScheduler;
    private Disposable subscription;

    public FavPhotoPresenter(Scheduler scheduler) {
        this.uiScheduler = scheduler;
        this.favPhotos = new ArrayList<>();

    }

    @Override
    public void attachView(FavFragmentView view) {
        super.attachView(view);
        Timber.d("FP AttachView");
//        repository.getUpdatedPhotos();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber.d("FirstAttach FP Presenter");
        super.onFirstViewAttach();
        photos = repository.getPhotoCollection();
        getViewState().initUI();
        photoObserver = new Observer<RepoEvents>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscription = d;
                Timber.d("Fav Presenter on Subscribe");
            }

            @Override
            public void onNext(RepoEvents events) {
                if (events == RepoEvents.UPDATE) {
                    favoriteIsChanged();
                    getViewState().updateRecyclerViewAdapter();
                }
            }

            @Override
            public void onError(Throwable t) {
                Timber.d(t.getMessage(), "on Error: %s");
            }

            @Override
            public void onComplete() {
                Timber.d("on Complete");
                getViewState().updateRecyclerViewAdapter();
            }
        };
        repository
                .getObservablePhotos()
                .subscribeOn(Schedulers.computation())
                .observeOn(uiScheduler)
                .subscribe(photoObserver);
    }


    @Override
    public void onDestroy() {
        Timber.d("FP Presenter Destroyed");
        super.onDestroy();
        subscription.dispose();
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
        repository.favoriteIsChanged(pm);
    }
}
