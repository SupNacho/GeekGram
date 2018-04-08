package geekgram.supernacho.ru.presenters;

import java.io.File;
import java.io.IOException;

import io.reactivex.Single;

public interface IMainPresenter {
    String addPhoto();
    Single<File> createPhotoFile(File location, String child) throws IOException;
}
