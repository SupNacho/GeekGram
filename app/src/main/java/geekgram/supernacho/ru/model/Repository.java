package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class Repository implements IRepository {
    private List<PhotoModel> photos;
    private PublishSubject<RepoEvents> photosObservable;
    private Disposable repDsposable;
    DbRepository dbRepository;
    NetRepository netRepository;


    public Repository(DbRepository dbRepository, NetRepository netRepository) {
        this.dbRepository = dbRepository;
        this.netRepository = netRepository;
        photos = new ArrayList<>();
        photosObservable = PublishSubject.create();
    }

    @Override
    public void addPhoto(boolean isFavorite, String uriString) {
        Timber.d("Try add realm photo");
        dbRepository.addPhoto(isFavorite, uriString);
    }

    @Override
    public void addPhoto(int pos, PhotoModel pm) {
        Timber.d("add photo to realm scn fn");
        dbRepository.addPhoto(pos, pm);
    }

    @Override
    public void remove(int pos) {
        Timber.d("Remove photo from realm");
        dbRepository.remove(pos);
    }

    @Override
    public void getUpdatedPhotos() {
        photos.clear();
        repDsposable = Observable.merge(netRepository.getNetPhoto(), dbRepository.getObservableDbPhotos())
                .subscribeOn(Schedulers.io()).subscribe(photoModels -> {
                    photos.addAll(photoModels);
                    photosObservable.onNext(RepoEvents.UPDATE);
                });
    }

    @Override
    public Observable<RepoEvents> getObservablePhotos() {
        return photosObservable;
    }

    @Override
    public void favoriteIsChanged(PhotoModel pm) {
        dbRepository.favoriteIsChanged(pm);
        getUpdatedPhotos();
    }

    @Override
    public List<PhotoModel> getPhotoCollection() {
        return photos;
    }

    @Override
    public void disposeTasks() {
        repDsposable.dispose();
    }
}
