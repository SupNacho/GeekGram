package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.AllPhotoFragmentView;
import geekgram.supernacho.ru.model.IImageCache;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@InjectViewState
public class AllPhotoPresenter extends MvpPresenter<AllPhotoFragmentView> implements IFragmentPresenter {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private FlowableSubscriber<List<PhotoModel>> photoObserver;
    private Scheduler uiScheduler;
    private Subscription subscription;

    @Inject
    IRepository repository;
    @Inject
    IImageCache imageCache;

    public AllPhotoPresenter(Scheduler scheduler) {
        this.photos = new ArrayList<>();
        this.uiScheduler = scheduler;
    }

    @Override
    public void attachView(AllPhotoFragmentView view) {
        super.attachView(view);
        Timber
                .tag("++")
                .d("AP AttachView");
        repository.getStartData();
    }

    @Override
    protected void onFirstViewAttach() {
        Timber
                .tag("++")
                .d("FirstAttach AP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
        photoObserver = new FlowableSubscriber<List<PhotoModel>>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
                Timber
                        .tag("++")
                        .d("APPresenter on Subscribe");
            }

            @Override
            public void onNext(List<PhotoModel> photoModels) {
                Timber.tag("++").d("Size %d", photoModels.size());
                photos.clear();
                photos.addAll(photoModels);
                getViewState().updateRecyclerViewAdapter();
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                Timber
                        .tag("++")
                        .d(t.getMessage(), "on Error %s");
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
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(photoObserver);
    }

    @Override
    public void onDestroy() {
        Timber
                .tag("++")
                .d("AP Presenter Destroyed");
        super.onDestroy();
        subscription.cancel();
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
        repository.favoriteIsChanged(pm);
    }
}
