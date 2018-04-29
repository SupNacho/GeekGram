package geekgram.supernacho.ru.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.model.entity.RealmImage;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class Repository implements IRepository {
    private List<PhotoModel> photos;
    private PublishSubject<List<PhotoModel>> photosObservable;


    public Repository() {
        photos = new ArrayList<>();
        photosObservable = PublishSubject.create();
    }

    @Override
    public void addPhoto(boolean isFavorite, String uriString) {
        Timber.d("Try add realm photo");
        Realm realm = Realm.getDefaultInstance();
        RealmImage img  = realm.where(RealmImage.class).equalTo("imgUri", uriString).findFirst();
        realm.executeTransaction( innerRealm -> {
            if (img == null){
                Timber.d("Add photo to realm");
                RealmImage newImage = realm.createObject(RealmImage.class, uriString);
                newImage.setFavorites(isFavorite);
            } else {
                img.setFavorites(isFavorite);
            }
        });
        realm.close();
    }

    @Override
    public void addPhoto(int pos, PhotoModel pm) {
        Timber.d("add photo to realm scn fn");
        Realm realm = Realm.getDefaultInstance();
        RealmImage img  = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findFirst();
        realm.executeTransaction( innerRealm -> {
            if (img == null){
                RealmImage newImage = realm.createObject(RealmImage.class, pm.getPhotoSrc());
                newImage.setFavorites(pm.isFavorite());
            } else {
                img.setFavorites(pm.isFavorite());
            }
        });
        realm.close();
        photos.add(pos, pm);
        photosObservable.onNext(photos);
    }

    @Override
    public void remove(int pos) {
        Timber.d("Remove photo from realm");
        PhotoModel pm = photos.remove(pos);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findAll();
        realm.executeTransaction( innerRealm -> realmPhotos.deleteAllFromRealm());
        photosObservable.onNext(photos);
        realm.close();
//        File deleteFile = new File(pm.getPhotoSrc());
//        if (deleteFile.exists()){
//            deleteFile.delete();
//        }
    }

    @Override
    public List<PhotoModel> getPhotos() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).findAll();
        if (realmPhotos != null) {
            photos.clear();
            for (RealmImage realmPhoto : realmPhotos) {
                photos.add(new PhotoModel(realmPhoto.isFavorites(), realmPhoto.getImgUri()));
            }
        }
        realm.close();
        return photos;
    }

    @Override
    public Observable<List<PhotoModel>> getObservablePhotos() {
        return photosObservable;
    }

    public void getStartData() {
        Timber
                .tag("++")
                .d("Get START DATA!");
        getPhotos();
        photosObservable.onNext(photos);
    }

    @Override
    public void favoriteIsChanged(PhotoModel pm) {
        Realm realm = Realm.getDefaultInstance();
        RealmImage realmImage = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findFirst();
        realm.executeTransaction( innerRealm ->{
            if (realmImage != null) {
                realmImage.setFavorites(pm.isFavorite());
            }
        });
        realm.close();
        getPhotos();
        photosObservable.onNext(photos);
    }
}
