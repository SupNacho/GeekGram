package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.DbRepository;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.Repository;

@Singleton
@Module(includes = {ApiModule.class, NetRepoModule.class, DbRepoModule.class})
public class RepoModule {

    @Singleton
    @Provides
    public IRepository repository(DbRepository dbRepo, NetRepository netRepo){
        return new Repository(dbRepo, netRepo);
    }
}
