package geekgram.supernacho.ru;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import geekgram.supernacho.ru.di.AppComponent;
import geekgram.supernacho.ru.di.DaggerAppComponent;
import geekgram.supernacho.ru.di.modules.AppModule;
import geekgram.supernacho.ru.utils.MyRealmMigration;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class App extends Application {

    private static App instance;
    public static String[] requestToken = {"0000"};
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
        instance = this;
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("geekgram2")
                .schemaVersion(2)
                .migration(new MyRealmMigration())
                .build();
        Realm.setDefaultConfiguration(configuration);
        Stetho.initializeWithDefaults(this);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        RxJavaPlugins.setErrorHandler(throwable -> Timber.d("%s", throwable.getCause()));
    }

    public static App getInstance(){
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
