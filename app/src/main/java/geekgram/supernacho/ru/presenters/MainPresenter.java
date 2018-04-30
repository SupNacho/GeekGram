package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.MainView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.Repository;
import io.reactivex.Single;
import timber.log.Timber;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements IMainPresenter{

    private String currentPhotoPath;
    @Inject IRepository repository;

    public MainPresenter() {
    }

    @Override
    public String addPhoto() {
        if (currentPhotoPath != null) {
            repository.addPhoto(false, currentPhotoPath);
            return currentPhotoPath;
        }
        return currentPhotoPath;
    }

    @Override
    public Single<File> createPhotoFile(final File location, final String child) throws IOException{
        return Single.fromCallable(() -> {
            if (location.exists()) {
                Timber.d("No dir");
                File newDir = new File(location, child);
                if(newDir.mkdir()) Timber.d("Create dir");
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = new File(location, child);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            currentPhotoPath = "file:" + image.getAbsolutePath();
            return image;
        });
    }
}
