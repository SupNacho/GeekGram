package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import geekgram.supernacho.ru.FullScreenView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class FullScreenPresenter extends MvpPresenter<FullScreenView> implements IFullScreenPresenter{
    private IRepository repository;

    public FullScreenPresenter() {
        repository = Repository.getInstance();
    }

    @Override
    public void favoriteIsChanged(int pos) {
        PhotoModel pm = repository.getPhotos().get(pos);
        pm.setFavorite(!pm.isFavorite());
        repository.favoriteIsChanged();
    }
}
