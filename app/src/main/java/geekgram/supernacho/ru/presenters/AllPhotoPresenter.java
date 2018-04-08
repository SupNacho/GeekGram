package geekgram.supernacho.ru.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.AllPhotoFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;
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
    private IRepository repository;
    private io.reactivex.Observer<List<PhotoModel>> photoObserver;
    private Scheduler uiScheduler;

    public AllPhotoPresenter(Scheduler scheduler) {
        this.repository = Repository.getInstance();
        this.photos = new ArrayList<>();
        this.uiScheduler = scheduler;
        photoObserver = new Observer<List<PhotoModel>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Timber
                        .tag("++")
                        .d("on Subscribe");
            }

            @Override
            public void onNext(List<PhotoModel> photoModels) {
                photos.clear();
                photos.addAll(photoModels);
                getViewState().updateRecyclerViewAdapter();
            }

            @SuppressLint("TimberArgCount")
            @Override
            public void onError(Throwable e) {
                Timber
                        .tag("++")
                        .d(e, "on Error %s");
            }

            @Override
            public void onComplete() {
                Timber
                        .tag("++")
                        .d("on Complete");
                getViewState().updateRecyclerViewAdapter();
            }
        };
    }

    @Override
    protected void onFirstViewAttach() {
        Timber
                .tag("++")
                .d("FirstAttach AP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
        repository
                .getObservablePhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(photoObserver);
        repository.getStartData();
    }

    @Override
    public void onDestroy() {
        Timber
                .tag("++")
                .d("AP Presenter Destroyed");
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
        repository.addPhoto(tempPos, photoTmp);
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
        repository.favoriteIsChanged();
    }
}
