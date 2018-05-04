package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.DbRepository;

@Singleton
@Module
public class DbRepoModule {

    @Singleton
    @Provides
    public DbRepository repository(){
        return new DbRepository();
    }
}
