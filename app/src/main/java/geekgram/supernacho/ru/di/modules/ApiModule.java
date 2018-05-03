package geekgram.supernacho.ru.di.modules;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import geekgram.supernacho.ru.model.api.ApiConst;
import geekgram.supernacho.ru.model.api.ApiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
@Module
public class ApiModule {

    @Provides
    public ApiService api(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    public Retrofit retrofit(GsonConverterFactory gsonConverterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ApiConst.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new StethoInterceptor())
                .build();
    }

    @Provides
    public GsonConverterFactory gsonConverterFactory() {
        return GsonConverterFactory.create();
    }
}
