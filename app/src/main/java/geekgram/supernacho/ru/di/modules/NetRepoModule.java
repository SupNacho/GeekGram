package geekgram.supernacho.ru.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.NetRepository;
import geekgram.supernacho.ru.model.api.ApiService;

@Singleton
@Module(includes = {ApiModule.class})
public class NetRepoModule {

    @Singleton
    @Provides
    public NetRepository netRepository(ApiService apiService){
        return new NetRepository(apiService);
    }
}