package geekgram.supernacho.ru;

import android.app.Application;

import geekgram.supernacho.ru.di.AppComponent;
import geekgram.supernacho.ru.di.DaggerAppComponent;
import geekgram.supernacho.ru.di.modules.AppModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class App extends Application {

    private static App instance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        instance = this;
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("geekgram")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(configuration);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static App getInstance(){
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
