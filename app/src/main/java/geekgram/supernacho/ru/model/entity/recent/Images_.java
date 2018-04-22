
package geekgram.supernacho.ru.model.entity.recent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images_ {

    @SerializedName("low_resolution")
    @Expose
    private LowResolution__ lowResolution;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail_ thumbnail;
    @SerializedName("standard_resolution")
    @Expose
    private StandardResolution__ standardResolution;

    public LowResolution__ getLowResolution() {
        return lowResolution;
    }

    public void setLowResolution(LowResolution__ lowResolution) {
        this.lowResolution = lowResolution;
    }

    public Thumbnail_ getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail_ thumbnail) {
        this.thumbnail = thumbnail;
    }

    public StandardResolution__ getStandardResolution() {
        return standardResolution;
    }

    public void setStandardResolution(StandardResolution__ standardResolution) {
        this.standardResolution = standardResolution;
    }

}
