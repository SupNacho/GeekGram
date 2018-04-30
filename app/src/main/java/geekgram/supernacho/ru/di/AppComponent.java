package geekgram.supernacho.ru.di;

import javax.inject.Singleton;

import dagger.Component;
import geekgram.supernacho.ru.FullscreenPhotoActivity;
import geekgram.supernacho.ru.MainActivity;
import geekgram.supernacho.ru.PhotosFromNetFragment;
import geekgram.supernacho.ru.adapter.AllPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.adapter.FavPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.adapter.PhotoFromDbRecyclerViewAdapter;
import geekgram.supernacho.ru.adapter.PhotoFromNetRecyclerViewAdapter;
import geekgram.supernacho.ru.di.modules.AppModule;
import geekgram.supernacho.ru.di.modules.DbRepoModule;
import geekgram.supernacho.ru.di.modules.ImageGliderModule;
import geekgram.supernacho.ru.di.modules.NetRepoModule;
import geekgram.supernacho.ru.di.modules.RepoModule;
import geekgram.supernacho.ru.presenters.AllPhotoPresenter;
import geekgram.supernacho.ru.presenters.FavPhotoPresenter;
import geekgram.supernacho.ru.presenters.FullScreenPresenter;
import geekgram.supernacho.ru.presenters.MainPresenter;
import geekgram.supernacho.ru.presenters.PhotosFromDbPresenter;
import geekgram.supernacho.ru.presenters.PhotosFromNetPresenter;

@Singleton
@Component(modules = {AppModule.class, RepoModule.class, ImageGliderModule.class, NetRepoModule.class, DbRepoModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(FullscreenPhotoActivity activity);
    void inject(PhotosFromNetFragment fragment);
    void inject(MainPresenter presenter);
    void inject(AllPhotoPresenter presenter);
    void inject(FavPhotoPresenter presenter);
    void inject(FullScreenPresenter presenter);
    void inject(PhotosFromDbPresenter presenter);
    void inject(PhotosFromNetPresenter presenter);
    void inject(AllPhotoRecyclerViewAdapter adapter);
    void inject(FavPhotoRecyclerViewAdapter adapter);
    void inject(PhotoFromDbRecyclerViewAdapter adapter);
    void inject(PhotoFromNetRecyclerViewAdapter adapter);
}
