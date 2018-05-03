package geekgram.supernacho.ru.model;

import java.util.ArrayList;

import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.model.api.ApiService;
import geekgram.supernacho.ru.model.entity.Data;
import geekgram.supernacho.ru.model.entity.User;
import geekgram.supernacho.ru.model.entity.recent.Datum;
import geekgram.supernacho.ru.model.entity.recent.UserRecent;
import geekgram.supernacho.ru.utils.NetworkStatus;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import timber.log.Timber;

public class NetRepository {
    private ApiService apiService;

    public NetRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Observable<User> getUser(String token){
        if (NetworkStatus.isOnline() && !App.requestToken[0].equals("0000")){
            return apiService.getUsersSelf(token);
        } else {
            return Observable.create( e -> {
                User user = new User();
                Data data = new Data();
                data.setFullName("No connection");
                user.setData(data);
               e.onNext(user);
            });
        }
    }

    public Observable<UserRecent> getUserRecentData(String token){
        if (NetworkStatus.isOnline() && !App.requestToken[0].equals("0000")){
            return apiService.getUserRecent(token);
        } else {
            return Observable.create( e -> {
                UserRecent userRecent = new UserRecent();
                userRecent.setData(new ArrayList<>());
                e.onNext(userRecent);
            });
        }
    }

    public Observable<PhotoModel> getNetSinglePhotoOneByOne(){
        if (NetworkStatus.isOnline() && !App.requestToken[0].equals("0000")){
            return Observable.create( e ->{
                Observable<UserRecent> ur = apiService.getUserRecent(App.requestToken[0]);
                ur.forEach(userRecent -> {
                    for (Datum data : userRecent.getData()) {
                       e.onNext(new PhotoModel(false, data.getImages()
                                .getStandardResolution().getUrl(), data.getId()));
                    }
                });
            });
        } else {
            return Observable.create(Emitter::onComplete);
        }
    }
    public Observable<Void> deletePhoto(String id){
        if (NetworkStatus.isOnline() && !App.requestToken[0].equals("0000")){
            Timber.d("Try delete from instagram id: %s", id);
            return apiService.deleteMedia( id, App.requestToken[0]);
        } else {
            return Observable.create( e ->
                    Timber.d("Deletion not supported in offline"));
        }
    }
}
