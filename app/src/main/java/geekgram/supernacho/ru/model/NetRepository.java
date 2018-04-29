package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.model.api.ApiService;
import geekgram.supernacho.ru.model.entity.Data;
import geekgram.supernacho.ru.model.entity.User;
import geekgram.supernacho.ru.model.entity.recent.Datum;
import geekgram.supernacho.ru.model.entity.recent.UserRecent;
import geekgram.supernacho.ru.utils.NetworkStatus;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NetRepository {
    ApiService apiService;

    public NetRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Observable<User> getUser(String token){
        if (NetworkStatus.isOnline()){
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
        if (NetworkStatus.isOnline()){
            return apiService.getUserRecent(token);
        } else {
            return Observable.create( e -> {
                UserRecent userRecent = new UserRecent();
                userRecent.setData(new ArrayList<>());
                e.onNext(userRecent);
            });
        }
    }
    public Observable<List<PhotoModel>> getNetPhoto(){
        List<PhotoModel> netPhotos = new ArrayList<>();
        if (NetworkStatus.isOnline()){
            return Observable.create( e ->{
                Observable<UserRecent> ur = apiService.getUserRecent(App.requestToken[0]);
                ur.forEach(userRecent -> {
                    for (Datum data : userRecent.getData()) {
                       netPhotos.add(new PhotoModel(false, data.getImages()
                                .getStandardResolution().getUrl()));
                    }
                    e.onNext(netPhotos);
                });
            });
        } else {
            return Observable.create( e -> {
                e.onNext(netPhotos);
            });
        }
    }

    public void favoriteIsChanged(PhotoModel pm) {

    }
}
