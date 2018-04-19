package geekgram.supernacho.ru.di.modules;

import android.widget.ImageView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.model.image.android.ImageLoaderGlider;

@Singleton
@Module
public class ImageGliderModule {

    @Provides
    public IImageLoader<ImageView> imageLoaderGlider(){
        return new ImageLoaderGlider();
    }
}
