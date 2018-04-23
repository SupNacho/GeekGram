package geekgram.supernacho.ru.utils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class MyRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 2){
            schema.create("RealmCachedImage")
                    .addField("url", String.class)
                    .addField("path", String.class);
            oldVersion++;
        }
    }
}
