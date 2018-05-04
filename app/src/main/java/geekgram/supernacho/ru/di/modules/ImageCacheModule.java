package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.IImageCache;
import geekgram.supernacho.ru.model.InstagramCache;

@Singleton
@Module
public class ImageCacheModule {
    @Provides
    public IImageCache imageCache(){
        return new InstagramCache();
    }
}
