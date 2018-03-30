package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.io.File;
import java.io.IOException;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface IMainPresenter {
    String addPhoto();
    File createPhotoFile(File location, String child) throws IOException;
}
