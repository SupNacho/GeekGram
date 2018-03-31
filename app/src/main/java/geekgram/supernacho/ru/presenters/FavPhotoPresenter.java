package geekgram.supernacho.ru.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import geekgram.supernacho.ru.FavFragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class FavPhotoPresenter extends MvpPresenter<FavFragmentView> implements IFragmentPresenter,
        Observer{
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private IRepository repository;

    public FavPhotoPresenter() {
        this.repository = Repository.getInstance();
        repository.addObserver(this);
        this.photos = new ArrayList<>();
        this.photos.addAll(repository.getPhotos());
        this.favPhotos = new ArrayList<>();
        favoriteIsChanged();
        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    public void viewPhoto(int pos) {
        PhotoModel pm;
        if (favPhotos != null && favPhotos.size() > pos) {
            if ((pm = favPhotos.get(pos)) != null && pm.getPhotoSrc() != null) {
                int tmpPos = photos.indexOf(pm);
                getViewState().startViewPhoto(tmpPos, pm.getPhotoSrc(), pm.isFavorite());
            }
        }
    }

    @Override
    public void deleteDialog(int pos) {
        getViewState().deletePhoto(pos);
    }

    @Override
    public void deletePhoto(int pos) {
        if (photos != null && photos.size() > pos) {
            photoTmp = favPhotos.get(pos);
            tempPos = photos.indexOf(photoTmp);
            repository.remove(tempPos);
            syncPhotoList();
//            getViewState().updateRecyclerViewAdapter();
        }
    }

    @Override
    public void undoDeletion() {
//        photos.add(tempPos, photoTmp);
        repository.addPhoto(tempPos, photoTmp);
        favoriteIsChanged();
        syncPhotoList();
//        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public List<PhotoModel> getFavPhotos() {
        return favPhotos;
    }

    @Override
    public void favoriteIsChanged() {
        favPhotos.clear();
        for (PhotoModel photo : photos) {
            if (photo.isFavorite()) favPhotos.add(photo);
        }
    }

    @Override
    public void setFavorite(PhotoModel pm) {
        pm.setFavorite(!pm.isFavorite());
        if (pm.isFavorite()) favPhotos.add(pm);
        else favPhotos.remove(pm);
        repository.favoriteIsChanged();
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("++", "FavUpdates");
        syncPhotoList();
        favoriteIsChanged();
        getViewState().updateRecyclerViewAdapter();
    }

    @Override
    public void syncPhotoList() {
        photos.clear();
        photos.addAll(repository.getPhotos());
        favoriteIsChanged();
    }
}
