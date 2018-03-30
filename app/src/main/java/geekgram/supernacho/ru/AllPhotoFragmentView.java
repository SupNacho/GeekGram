package geekgram.supernacho.ru;

import com.arellomobile.mvp.MvpView;

public interface AllPhotoFragmentView extends MvpView {
    void updateRecyclerViewAdapter();
    void startViewPhoto(int pos, String uri, boolean isFavorite);
}
