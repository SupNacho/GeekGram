package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.FavFragmentView;
import geekgram.supernacho.ru.model.DbRepository;
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
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    @Inject
    DbRepository repository;
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
        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber.d("FirstAttach FP Presenter");
        super.onFirstViewAttach();
        photos = repository.getPhotoCollection();
        getViewState().initUI();
        Observer<RepoEvents> dbRepoEventBusObserver = new Observer<RepoEvents>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscription = d;
                Timber.d("Fav Presenter on Subscribe");
            }

            @Override
            public void onNext(RepoEvents events) {
                Timber.d("Fav event %s", events);
                if (events == RepoEvents.DB_UPDATED) {
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
                .getDbRepoEventBus()
                .subscribeOn(Schedulers.computation())
                .observeOn(uiScheduler)
                .subscribe(dbRepoEventBusObserver);
    }


    @Override
    public void onDestroy() {
        Timber.d("FP Presenter Destroyed");
        if (subscription != null) subscription.dispose();
            super.onDestroy();
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
            int tempPos = photos.indexOf(photoTmp);
            repository.remove(tempPos);
        }
    }

    @Override
    public void undoDeletion() {
        repository.undoDeletion(photoTmp);
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
