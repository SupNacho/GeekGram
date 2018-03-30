package geekgram.supernacho.ru.model;


public class PhotoModel {
    private boolean isFavorite;
    private String photoSrc;

    public PhotoModel(boolean isFavorite, String photoSrcUri) {
        this.isFavorite = isFavorite;
        this.photoSrc = photoSrcUri;
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
}
