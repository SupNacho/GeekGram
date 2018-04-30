package geekgram.supernacho.ru.di.modules;

import android.widget.ImageView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.IImageCache;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.model.image.android.ImageLoaderGlider;

@Singleton
@Module(includes = {ImageCacheModule.class})
public class ImageGliderModule {

    @Provides
    public IImageLoader<ImageView> imageLoaderGlider(IImageCache cache){
        return new ImageLoaderGlider(cache);
    }
}
