package geekgram.supernacho.ru.presenters;

import java.util.List;

import geekgram.supernacho.ru.model.PhotoModel;

public interface INetFragmentPresenter {

    void viewPhoto(int pos);
    List<PhotoModel> getPhotos();
    List<PhotoModel> getFavPhotos();
    void setFavorite(PhotoModel pm);
    void favoriteIsChanged();
    void loadUserData(String token);
    void loadUserRecent(String token);
}
