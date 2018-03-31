package geekgram.supernacho.ru.presenters;

public interface IPhotoListPresenter {
    int pos = -1;
    void bindView(IListView view);
    int getViewCount();
}
