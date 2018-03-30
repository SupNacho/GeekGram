package geekgram.supernacho.ru.model;

import java.util.List;

public interface IRepository {
    void addPhoto(boolean isFavorite, String uriString);
    List<PhotoModel> getPhotos();
}
