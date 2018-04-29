package geekgram.supernacho.ru.model;

import java.util.List;
import java.util.Observer;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface IRepository {
    void addPhoto(boolean isFavorite, String uriString);
    void addPhoto(int pos, PhotoModel pm);
    void remove(int pos);
    void undoDeletion(PhotoModel pm);
    void favoriteIsChanged(PhotoModel pm);
    Observable<RepoEvents> getObservablePhotos();
    void getUpdatedPhotos();
    List<PhotoModel> getPhotoCollection();
    void disposeTasks();
    int getCount();
}
