package geekgram.supernacho.ru.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface FavFragmentView extends MvpView {
    void initUI();
    void updateRecyclerViewAdapter();
    void deletePhoto(final int pos);
    void startViewPhoto(int pos, String uri, boolean isFavorite);
}
