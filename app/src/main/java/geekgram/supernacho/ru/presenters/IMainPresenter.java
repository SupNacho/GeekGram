package geekgram.supernacho.ru.presenters;

import java.io.File;
import java.io.IOException;

public interface IMainPresenter {
    String addPhoto();
    File createPhotoFile(File location, String child) throws IOException;
}
