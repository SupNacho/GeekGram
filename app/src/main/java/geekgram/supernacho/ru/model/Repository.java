package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class Repository implements IRepository {
    private List<PhotoModel> photos;
    private PublishSubject<RepoEvents> mainRepoEventBus;
    private Disposable repDisposable;
    private Disposable disposeSubsDb;
    private int tmpPos;
    private PhotoModel tmpPhoto;
    DbRepository dbRepository;
    NetRepository netRepository;


    public Repository(DbRepository dbRepository, NetRepository netRepository) {
        this.dbRepository = dbRepository;
        this.netRepository = netRepository;
        Timber.d(" ----- OO ----- Repository CREATED");
        photos = new ArrayList<>();
        mainRepoEventBus = PublishSubject.create();
        Observer<RepoEvents> dbRepoEventBusObserver = new Observer<RepoEvents>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposeSubsDb = d;
            }

            @Override
            public void onNext(RepoEvents events) {
                if (events == RepoEvents.DB_UPDATED) {
                    mainRepoEventBus.onNext(RepoEvents.UPDATE);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        dbRepository.getDbRepoEventBus()
                .subscribeOn(Schedulers.io())
                .subscribe(dbRepoEventBusObserver);
    }

    @Override
    public void addPhoto(boolean isFavorite, String uriString) {
        Timber.d("Try add realm photo");
        photos.add(new PhotoModel(isFavorite, uriString));
        dbRepository.addPhoto(isFavorite, uriString);
    }

    @Override
    public void addPhoto(int pos, PhotoModel pm) {
        Timber.d("add photo to realm scn fn");
        photos.add(pos, pm);
        dbRepository.addPhoto(pos, pm);
    }

    @Override
    public void undoDeletion(PhotoModel pm) {
        photos.add(tmpPos, tmpPhoto);
        dbRepository.undoDeletion(pm);
        mainRepoEventBus.onNext(RepoEvents.UPDATE);
    }

    @Override
    public void remove(int pos) {
        Timber.d("Remove photo from realm");
        tmpPos = pos;
        dbRepository.remove(photos.get(pos));
        tmpPhoto = photos.remove(pos);
    }

    @Override
    public void doCollectionsMerge() {
        photos.clear();
        repDisposable = Observable.merge(dbRepository.getObservableSingleDbPhotosOneByOne(),
                netRepository.getNetSinglePhotoOneByOne())
                .subscribeOn(Schedulers.io()).subscribe(photoModels -> {
                    photos.add(photoModels);
                    mainRepoEventBus.onNext(RepoEvents.UPDATE);
                });
    }

    @Override
    public Observable<RepoEvents> getEvenBus() {
        return mainRepoEventBus;
    }

    @Override
    public void favoriteIsChanged(PhotoModel pm) {
        dbRepository.favoriteIsChanged(pm);
    }

    @Override
    public List<PhotoModel> getPhotoCollection() {
        return photos;
    }

    @Override
    public void disposeTasks() {
        if (repDisposable != null) repDisposable.dispose();
        if (disposeSubsDb != null) disposeSubsDb.dispose();
    }
}
