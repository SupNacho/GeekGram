package geekgram.supernacho.ru.presenters;

public interface IAllPhotoListPresenter {
    int pos = -1;

    void bindView(IListView view);
    int getViewCount();
}
