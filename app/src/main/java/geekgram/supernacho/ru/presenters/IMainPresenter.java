package geekgram.supernacho.ru.presenters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import geekgram.supernacho.ru.model.PhotoModel;

public interface IMainPresenter {
    String addPhoto();
    File createPhotoFile(File location, String child) throws IOException;
    List<PhotoModel> getPhotos();
    List<PhotoModel> getFavPhotos();
}
