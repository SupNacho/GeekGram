package geekgram.supernacho.ru.model;


public class PhotoModel {
    private boolean isFavorite;
    private String photoSrc;
    private String photoId;

    public PhotoModel(boolean isFavorite, String photoSrcUri) {
        this.isFavorite = isFavorite;
        this.photoSrc = photoSrcUri;
        this.photoId = "db_stored";
    }
    public PhotoModel(boolean isFavorite, String photoSrcUri, String id) {
        this.isFavorite = isFavorite;
        this.photoSrc = photoSrcUri;
        this.photoId = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public String getPhotoId() {
        return photoId;
    }
}
