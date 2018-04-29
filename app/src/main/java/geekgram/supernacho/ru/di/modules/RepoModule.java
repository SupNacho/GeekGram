package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.DbRepository;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.Repository;
import geekgram.supernacho.ru.model.api.ApiService;

@Singleton
@Module(includes = {ApiModule.class, NetRepoModule.class, DbRepoModule.class})
public class RepoModule {

    @Singleton
    @Provides
    public IRepository repository(DbRepository dbRepo, NetRepository netRepo){
        return new Repository(dbRepo, netRepo);
    }

//    @Provides
//    public DbRepository dbRepo(){
//        return new DbRepository();
//    }
//    @Provides
//    public NetRepository netRepo(ApiService apiService){
//        return new NetRepository(apiService);
//    }
}
