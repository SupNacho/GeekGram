package geekgram.supernacho.ru.model;

import java.util.List;
import java.util.Observer;

public interface IRepository {
    void addObserver(Observer o);
    void addPhoto(boolean isFavorite, String uriString);
    void addPhoto(int pos, PhotoModel pm);
    void remove(int pos);
    void favoriteIsChanged();
    List<PhotoModel> getPhotos();
}
