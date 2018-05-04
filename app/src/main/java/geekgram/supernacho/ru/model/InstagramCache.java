package geekgram.supernacho.ru.model;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import geekgram.supernacho.ru.App;
import geekgram.supernacho.ru.model.entity.RealmCachedImage;
import geekgram.supernacho.ru.model.entity.RealmImage;
import io.realm.Realm;
import timber.log.Timber;

public class IstagramCache implements IImageCache{
    private static final String IMAGE_FOLDER_NAME = "inst_image";

    @Override
    public File getFile(String url) {
        RealmCachedImage cachedImage = Realm.getDefaultInstance().where(RealmCachedImage.class).equalTo("url", url).findFirst();
        if (cachedImage != null){
            return new File(cachedImage.getPath());
        }
        return null;
    }

    @Override
    public boolean contains(String url) {
        return Realm.getDefaultInstance().where(RealmCachedImage.class).equalTo("url", url).count() > 0;
    }

    @Override
    public void clear() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.delete(RealmCachedImage.class));
        deleteFileOrDirRecursive(getImageDir());
    }

    @Override
    public File saveImage(String url, Bitmap bitmap) {
        if (!getImageDir().exists() && !getImageDir().mkdirs())
        {
            throw new RuntimeException("Failed to create directory: " + getImageDir().toString());
        }

        final String fileFormat = url.contains(".jpg") ? ".jpg" : ".png";
        final File imageFile = new File(getImageDir(), MD5(url) + fileFormat);
        FileOutputStream fos;

        try
        {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(fileFormat.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (Exception e)
        {
            Timber.d("Failed to save image");
            return null;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmImage realmImage = realm.where(RealmImage.class).equalTo("imgUri", imageFile.toString()).findFirst();

            realm.executeTransaction( innerRealm -> {
                if (realmImage == null){
                    RealmImage newImage = realm.createObject(RealmImage.class, imageFile.toString());
                }
            });
        realm.close();

//        Realm.getDefaultInstance().executeTransactionAsync(realm ->
//        {
//            RealmCachedImage cachedImage = new RealmCachedImage();
//            cachedImage.setUrl(url);
//            cachedImage.setPath(imageFile.toString());
//            realm.copyToRealm(cachedImage);
//        });

        return imageFile;
    }

    @Override
    public File getImageDir() {
        return new File(App.getInstance().getExternalFilesDir(null) + "/" + IMAGE_FOLDER_NAME);
    }

    @Override
    public String MD5(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(m).update(s.getBytes(),0,s.length());
        return new BigInteger(1, m.digest()).toString(16);
    }

    @Override
    public float getSizeKb() {
        return getFileOrDirSize(getImageDir()) / 1024f;
    }

    @Override
    public void deleteFileOrDirRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
        {
            for (File child : fileOrDirectory.listFiles())
            {
                deleteFileOrDirRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    public long getFileOrDirSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFileOrDirSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }
}
