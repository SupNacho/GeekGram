package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import javax.inject.Inject;

import geekgram.supernacho.ru.FullScreenView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class FullScreenPresenter extends MvpPresenter<FullScreenView> implements IFullScreenPresenter{
    @Inject IRepository repository;

    public FullScreenPresenter() {
    }

    @Override
    public void favoriteIsChanged(int pos) {
        PhotoModel pm = repository.getPhotos().get(pos);
        pm.setFavorite(!pm.isFavorite());
        repository.favoriteIsChanged(pm);
    }
}
