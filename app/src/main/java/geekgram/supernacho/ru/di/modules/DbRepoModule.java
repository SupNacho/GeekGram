package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.DbRepository;
import geekgram.supernacho.ru.model.IRepository;

@Singleton
@Module
public class DbRepoModule {

    @Singleton
    @Provides
    public DbRepository repository(){
        return new DbRepository();
    }
}
