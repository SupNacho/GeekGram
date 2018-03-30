package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import geekgram.supernacho.ru.model.PhotoModel;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface IMainPresenter {
    String addPhoto();
    File createPhotoFile(File location, String child) throws IOException;
    List<PhotoModel> getPhotos();
    List<PhotoModel> getFavPhotos();
}
