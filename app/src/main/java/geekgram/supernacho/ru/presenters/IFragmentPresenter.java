package geekgram.supernacho.ru.presenters;

import java.util.List;

import geekgram.supernacho.ru.model.PhotoModel;

public interface IFragmentPresenter {

    void viewPhoto(int pos);
    void deletePhoto(int pos);
    void undoDeletion();
    void  deleteDialog(int pos);
    List<PhotoModel> getPhotos();
    List<PhotoModel> getFavPhotos();
    void setFavorite(PhotoModel pm);
    void favoriteIsChanged();
}
