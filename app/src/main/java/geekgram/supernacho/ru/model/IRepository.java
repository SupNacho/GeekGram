package geekgram.supernacho.ru.model;

import java.util.List;
import java.util.Observer;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface IRepository {
    void getStartData();
    void addPhoto(boolean isFavorite, String uriString);
    void addPhoto(int pos, PhotoModel pm);
    void remove(int pos);
    void favoriteIsChanged(PhotoModel pm);
    Flowable<List<PhotoModel>> getObservablePhotos();
    List<PhotoModel> getPhotos();
}
