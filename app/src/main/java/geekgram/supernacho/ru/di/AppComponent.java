package geekgram.supernacho.ru.di;

import android.widget.ImageView;

import javax.inject.Singleton;

import dagger.Component;
import geekgram.supernacho.ru.FullscreenPhotoActivity;
import geekgram.supernacho.ru.MainActivity;
import geekgram.supernacho.ru.adapter.AllPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.adapter.FavPhotoRecyclerViewAdapter;
import geekgram.supernacho.ru.di.modules.AppModule;
import geekgram.supernacho.ru.di.modules.ImageGliderModule;
import geekgram.supernacho.ru.di.modules.RepoModule;
import geekgram.supernacho.ru.presenters.AllPhotoPresenter;
import geekgram.supernacho.ru.presenters.FavPhotoPresenter;
import geekgram.supernacho.ru.presenters.FullScreenPresenter;
import geekgram.supernacho.ru.presenters.MainPresenter;

@Singleton
@Component(modules = {AppModule.class, RepoModule.class, ImageGliderModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(FullscreenPhotoActivity activity);
    void inject(MainPresenter presenter);
    void inject(AllPhotoPresenter presenter);
    void inject(FavPhotoPresenter presenter);
    void inject(FullScreenPresenter presenter);
    void inject(AllPhotoRecyclerViewAdapter adapter);
    void inject(FavPhotoRecyclerViewAdapter adapter);
}
