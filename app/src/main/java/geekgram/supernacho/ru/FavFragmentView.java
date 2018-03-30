package geekgram.supernacho.ru;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

//@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface AllPhotoFragmentView extends MvpView {
    void updateRecyclerViewAdapter();
    void startViewPhoto(int pos, String uri, boolean isFavorite);
}
