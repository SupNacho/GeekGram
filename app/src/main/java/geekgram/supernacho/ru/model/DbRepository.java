package geekgram.supernacho.ru.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import geekgram.supernacho.ru.model.entity.RealmImage;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class DbRepository {
    private List<PhotoModel> photos;
    private PublishSubject<RepoEvents> dbRepoEventBus;
    private int tmpPos;
    private Observable<File> deleteProcessor;
    private Observer<File> deleteProcessObserver;
    private Disposable disposeDeleteProcessor;


    public DbRepository() {
        photos = new ArrayList<>();
        dbRepoEventBus = PublishSubject.create();
        deleteProcessObserver = new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposeDeleteProcessor = d;

            }

            @Override
            public void onNext(File file) {
                Timber.d("File Path: %s", file);
                Timber.d("++++ File exist %s", file.exists());
                if (file.exists()) Timber.d("File deleted %s", file.delete());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
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
        dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
    }

    public void undoDeletion(PhotoModel pm) {
        Timber.d("undo deletion");
        disposeDeleteProcessor.dispose();
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
        dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
    }

    public void remove(PhotoModel pm) {
        Timber.d("Remove photo from realm");
        tmpPos = photos.indexOf(pm);
        boolean removed = photos.remove(pm);
        if (removed) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).equalTo("imgUri", pm.getPhotoSrc()).findAll();
            realm.executeTransaction(innerRealm -> realmPhotos.deleteAllFromRealm());
            dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
            realm.close();
            deleteProcessor = Observable.create(e -> e.onNext(new File(pm.getPhotoSrc().substring(5,pm.getPhotoSrc().length()))));
            deleteProcessor.subscribeOn(Schedulers.io()).delay(5, TimeUnit.SECONDS).subscribe(deleteProcessObserver);
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
                dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
            }
        }
        realm.close();
    }

    public Observable<PhotoModel> getObservableSingleDbPhotosOneByOne() {
        return Observable.create( e -> {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmImage> realmPhotos = realm.where(RealmImage.class).findAll();
            if (realmPhotos != null) {
                photos.clear();
                for (RealmImage realmPhoto : realmPhotos) {
                    PhotoModel pm = new PhotoModel(realmPhoto.isFavorites(), realmPhoto.getImgUri());
                    photos.add(pm);
                    e.onNext(pm);
                    dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
                }
            }
            realm.close();
        });
    }


    public Observable<RepoEvents> getDbRepoEventBus() {
        return dbRepoEventBus;
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
        dbRepoEventBus.onNext(RepoEvents.DB_UPDATED);
    }

    public List<PhotoModel> getPhotoCollection() {
        return photos;
    }
}
