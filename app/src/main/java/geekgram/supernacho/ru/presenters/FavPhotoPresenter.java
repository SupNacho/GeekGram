package geekgram.supernacho.ru.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.FragmentView;
import geekgram.supernacho.ru.model.IRepository;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.Repository;

@InjectViewState
public class AllPhotoPresenter extends MvpPresenter<FragmentView> implements IFragmentPresenter {
    private PhotoModel photoTmp;
    private int tempPos;
    private List<PhotoModel> photos;
    private IRepository repository;

    public AllPhotoPresenter() {
        this.repository = Repository.getInstance();
        this.photos = repository.getPhotos();
    }

    @Override
    public void addPhoto(String photoUriString) {
        repository.addPhoto(false, photoUriString);
//        photos.add(new PhotoModel(false, photoUriString));
        getViewState().updateRecyclerViewAdapter();
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

    @Override
    public void deletePhoto(int pos) {
        if (photos != null && photos.size() > pos) {
            photoTmp = photos.remove(pos);
            tempPos = pos;
            repository.remove(pos);
            getViewState().updateRecyclerViewAdapter();
        }
    }

    @Override
    public void undoDeletion() {
        photos.add(tempPos, photoTmp);
        repository.addPhoto(tempPos, photoTmp);
        getViewState().updateRecyclerViewAdapter();
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
//        favPhotos.clear();
//        for (PhotoModel photo : photos) {
//            if (photo.isFavorite()) favPhotos.add(photo);
//        }
    }
}
