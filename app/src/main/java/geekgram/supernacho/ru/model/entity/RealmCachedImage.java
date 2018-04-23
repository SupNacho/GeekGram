package geekgram.supernacho.ru.model.entity;

import io.realm.RealmObject;

public class RealmCachedImage extends RealmObject {
        String url;
        String path;

        public void setUrl(String url)
        {
            this.url = url;
        }

        public void setPath(String path)
        {
            this.path = path;
        }

        public String getPath()
        {
            return path;
        }
}
