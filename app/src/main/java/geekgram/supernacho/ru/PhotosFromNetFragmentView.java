package geekgram.supernacho.ru;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface PhotosFromNetFragmentView extends MvpView {
    void initUI();
    void updateRecyclerViewAdapter();
    void startViewPhoto(int pos, String uri, boolean isFavorite);
    void loadUserData(String avatarUri, String fullName);
    void onHttpException(String error);
}
