package geekgram.supernacho.ru.model.api;

import geekgram.supernacho.ru.model.entity.User;
import geekgram.supernacho.ru.model.entity.recent.UserRecent;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v1/users/self/media/recent/")
    Observable<UserRecent> getUserRecent(@Query("access_token") String accessToken);

    @GET("v1/users/self/")
    Observable<User> getUsersSelf(@Query("access_token") String accessToken);
}
