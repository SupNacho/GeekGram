package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

import geekgram.supernacho.ru.model.entity.RealmImage;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DbRepository {
    private List<PhotoModel> photos;
    private PublishSubject<RepoEvents> photosObservable;
    private int tmpPos;


    public DbRepository() {
        photos = new ArrayList<>();
        photosObservable = PublishSubject.create();
    }


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
        photosObservable.onNext(RepoEvents.DB_UPDATED);
    }

    public void undoDeletion(PhotoModel pm) {
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
        if (tmpPos > -1)photos.add(tmpPos, pm);
        photosObservable.onNext(RepoEvents.DB_UPDATED);
    }

    public void remove(int pos) {
        Timber.d("Remove photo from realm");
        PhotoModel pm = photos.remove(pos);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findAll();
        realm.executeTransaction( innerRealm -> realmPhotos.deleteAllFromRealm());
//        photosObservable.onNext(RepoEvents.UPDATE);
        photosObservable.onNext(RepoEvents.DB_UPDATED);
        realm.close();
    }

    public void remove(PhotoModel pm) {
        Timber.d("Remove photo from realm");
        tmpPos = photos.indexOf(pm);
        boolean removed = photos.remove(pm);
        if (removed) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findAll();
            realm.executeTransaction(innerRealm -> realmPhotos.deleteAllFromRealm());
//            photosObservable.onNext(RepoEvents.UPDATE);
            photosObservable.onNext(RepoEvents.DB_UPDATED);
            realm.close();
        } else {
            Timber.d("Trying delete Instagram photo");
        }
    }


    public void getPhotos() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).findAll();
        if (realmPhotos != null) {
            photos.clear();
            for (RealmImage realmPhoto : realmPhotos) {
                photos.add(new PhotoModel(realmPhoto.isFavorites(), realmPhoto.getImgUri()));
                photosObservable.onNext(RepoEvents.DB_UPDATED);
            }
        }
        realm.close();
    }

    public Observable<List<PhotoModel>> getObservableDbPhotos() {
        return Observable.create( e -> {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).findAll();
            if (realmPhotos != null) {
                photos.clear();
                for (RealmImage realmPhoto : realmPhotos) {
                    photos.add(new PhotoModel(realmPhoto.isFavorites(), realmPhoto.getImgUri()));
                }
            }
            realm.close();
            e.onNext(photos);
        });
    }


    public Observable<RepoEvents> getObservablePhotos() {
        return photosObservable;
    }

    public void favoriteIsChanged(PhotoModel pm) {
        Realm realm = Realm.getDefaultInstance();
        RealmImage realmImage = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findFirst();
        realm.executeTransaction( innerRealm ->{
            if (realmImage != null) {
                realmImage.setFavorites(pm.isFavorite());
            }
        });
        realm.close();
        photosObservable.onNext(RepoEvents.DB_UPDATED);
    }

    public List<PhotoModel> getPhotoCollection() {
        return photos;
    }
}
