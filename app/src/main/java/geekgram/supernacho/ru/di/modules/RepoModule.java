package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.Repository;

@Singleton
@Module
public class RepoModule {

    @Singleton
    @Provides
    public IRepository repository(){
        return new Repository();
    }
}
