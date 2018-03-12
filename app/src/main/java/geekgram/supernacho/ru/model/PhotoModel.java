package geekgram.supernacho.ru.model;


import android.net.Uri;


public class PhotoModel {
    private boolean isFavorite;
    private Uri photoSrc;

    public PhotoModel(boolean isFavorite, Uri photoSrc) {
        this.isFavorite = isFavorite;
        this.photoSrc = photoSrc;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public Uri getPhotoSrc() {
        return photoSrc;
    }
}
