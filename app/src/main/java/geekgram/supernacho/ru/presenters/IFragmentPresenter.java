package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import geekgram.supernacho.ru.model.PhotoModel;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface IFragmentPresenter {

    void addPhoto(String photoUriString);
    void viewPhoto(int pos);
    void deletePhoto(int pos);
    void undoDeletion();
    List<PhotoModel> getPhotos();
    List<PhotoModel> getFavPhotos();
    void setFavorite(PhotoModel pm);
    void favoriteIsChanged();
}
