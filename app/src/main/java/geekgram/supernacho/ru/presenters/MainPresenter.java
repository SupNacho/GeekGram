package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import geekgram.supernacho.ru.MainView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements IMainPresenter{

    private String currentPhotoPath;
    private IRepository repository;

    public MainPresenter() {
        repository = Repository.getInstance();
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
    public File createPhotoFile(File location, String child) throws IOException{
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
    }
}
