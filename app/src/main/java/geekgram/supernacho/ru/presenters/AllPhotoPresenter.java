package geekgram.supernacho.ru.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import geekgram.supernacho.ru.AllPhotoFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class AllPhotoPresenter extends MvpPresenter<AllPhotoFragmentView> implements IFragmentPresenter,
        Observer {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private IRepository repository;

    public AllPhotoPresenter() {
        this.repository = Repository.getInstance();
        this.photos = new ArrayList<>();
        photos.addAll(repository.getPhotos());
    }

    @Override
    protected void onFirstViewAttach() {
        Log.d("++", "FirstAttach AP Presenter");
        super.onFirstViewAttach();
        getViewState().initUI();
        repository.addObserver(this);
    }

    @Override
    public void onDestroy() {
        Log.d("++", "AP Presenter Destroyed");
        super.onDestroy();
        repository.deleteObserver(this);
    }

    @Override
    public void viewPhoto(int pos) {
        PhotoModel pm;
        if (photos != null && photos.size() > pos){
            if ((pm = photos.get(pos)) != null && pm.getPhotoSrc() != null){
                getViewState().startViewPhoto(pos, pm.getPhotoSrc(), pm.isFavorite());
            }
        }
    }

    public void  deleteDialog(int pos){
        getViewState().deletePhoto(pos);
    }
    @Override
    public void deletePhoto(int pos) {
        if (photos != null && photos.size() > pos) {
            photoTmp = photos.get(pos);
            tempPos = pos;
            repository.remove(pos);
            syncPhotoList();
        }
    }

    @Override
    public void undoDeletion() {
        repository.addPhoto(tempPos, photoTmp);
        syncPhotoList();
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public List<PhotoModel> getFavPhotos() {
        return new ArrayList<>();
    }

    @Override
    public void favoriteIsChanged() {
        // TODO: 31.03.2018 Вероятно надо разнести интерфейс фрагментов общегие фото и избранное на два разных. 
    }

    @Override
    public void setFavorite(PhotoModel pm) {
        pm.setFavorite(!pm.isFavorite());
        repository.favoriteIsChanged();
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("++", "AllPhoto Updated");
        syncPhotoList();
        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    public void syncPhotoList(){
        photos.clear();
        photos.addAll(repository.getPhotos());
    }
}
