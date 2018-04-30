package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.AllPhotoFragmentView;
import geekgram.supernacho.ru.model.DbRepository;
import geekgram.supernacho.ru.model.IImageCache;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.RepoEvents;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class AllPhotoPresenter extends MvpPresenter<AllPhotoFragmentView> implements IFragmentPresenter {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private Observer<RepoEvents> photoObserver;
    private Scheduler uiScheduler;
    private Disposable subscription;

    @Inject
    DbRepository dbRepository;
    @Inject
    IRepository repository;
    @Inject
    NetRepository netRepository;
    @Inject
    IImageCache imageCache;

    public AllPhotoPresenter(Scheduler scheduler) {
        this.uiScheduler = scheduler;
    }

    @Override
    public void attachView(AllPhotoFragmentView view) {
        super.attachView(view);
        Timber.d("AP AttachView");
        repository.getUpdatedPhotos();
        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber.d("FirstAttach AP Presenter");
        photos = repository.getPhotoCollection();
        super.onFirstViewAttach();
        getViewState().initUI();
        photoObserver = new Observer<RepoEvents>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscription = d;
                Timber.d("APPresenter on Subscribe");
            }

            @Override
            public void onNext(RepoEvents event) {
                Timber.d("Event %s", event);
                if (event == RepoEvents.UPDATE) getViewState().updateRecyclerViewAdapter();
            }

            @Override
            public void onError(Throwable t) {
                Timber.d(t.getMessage(), "on Error %s");
            }

            @Override
            public void onComplete() {
                Timber.d("on Complete");
                getViewState().updateRecyclerViewAdapter();
            }
        };

        repository
                .getObservablePhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(photoObserver);
    }

    @Override
    public void onDestroy() {
        Timber.d("AP Presenter Destroyed");
        if (subscription != null) subscription.dispose();
        super.onDestroy();
    }

    @Override
    public void viewPhoto(int pos) {
        PhotoModel pm;
        if (photos != null && photos.size() > pos){
            if ((pm = photos.get(pos)) != null && pm.getPhotoSrc() != null){
                getViewState().startViewPhoto(pos, pm.getPhotoSrc(), pm.isFavorite());
            }
        }
    }

    public void  deleteDialog(int pos){
        getViewState().deletePhoto(pos);
    }
    @Override
    public void deletePhoto(int pos) {
        if (photos != null && photos.size() > pos) {
            photoTmp = photos.get(pos);
            tempPos = pos;
            repository.remove(pos);
        }
    }

    @Override
    public void undoDeletion() {
        repository.undoDeletion(photoTmp);
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
        // TODO: 31.03.2018 Вероятно надо разнести интерфейс фрагментов общие фото и избранное на два разных.
    }

    @Override
    public void setFavorite(PhotoModel pm) {
        pm.setFavorite(!pm.isFavorite());
        repository.favoriteIsChanged(pm);
    }
}
