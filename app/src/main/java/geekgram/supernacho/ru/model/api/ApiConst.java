package geekgram.supernacho.ru.model.api;

public interface ApiConst {
    String BASE_URL = "https://api.instagram.com/";
    String REDIRECT_URI = "https://vanbellum.wixsite.com/photobro/";
    String CLIENT_ID = "9127ee4c7ec94be2971c62a203a40fce";
    int AUTH_BAD_REQUEST = 400;
    int AUTH_REQUIRED = 401;
    String NET_URI_STARTS = "http";
    String AUTH_URL = "https://api.instagram.com/oauth/authorize/" +
            "?client_id=" + ApiConst.CLIENT_ID +
            "&redirect_uri=" + ApiConst.REDIRECT_URI +
            "&response_type=token";
    String TOKEN_SPLIT_REGEX = "=";

}
