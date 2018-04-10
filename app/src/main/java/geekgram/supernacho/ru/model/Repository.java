package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public class Repository implements IRepository {
    private static final Repository ourInstance = new Repository();
    private List<PhotoModel> photos;
    private PublishSubject<List<PhotoModel>> photosObservable;

    public static Repository getInstance() {
        return ourInstance;
    }

    private Repository() {
        photos = new ArrayList<>();
        //Start Demo data
        boolean isFav;
        for (int i = 0; i < 3; i++) {
            isFav = (i % 3) == 0;
            photos.add(new PhotoModel(isFav, null));
        }
        photosObservable = PublishSubject.create();
        //End Demo data
    }

    @Override
    public void addPhoto(boolean isFavorite, String uriString) {
        photos.add(0, new PhotoModel(isFavorite, uriString));
        Timber
                .tag("++")
                .d("Photo Added Repository");
        photosObservable.onNext(photos);
    }

    @Override
    public void addPhoto(int pos, PhotoModel pm) {
        photos.add(pos, pm);
        photosObservable.onNext(photos);
    }

    @Override
    public void remove(int pos) {
        photos.remove(pos);
        photosObservable.onNext(photos);
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public Flowable<List<PhotoModel>> getObservablePhotos() {
        return photosObservable.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void getStartData() {
        Timber
                .tag("++")
                .d("Get START DATA!");
        photosObservable.onNext(photos);
    }

    @Override
    public void favoriteIsChanged() {
        photosObservable.onNext(photos);
    }
}
