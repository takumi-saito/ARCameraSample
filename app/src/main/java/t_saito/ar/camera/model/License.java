package t_saito.ar.camera.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class License {

    @NonNull private final String title;
    @NonNull private final String message;
    @NonNull private final String url;

    private License(@NonNull String title,@NonNull String message,@NonNull String url) {
        this.title = title;
        this.message = message;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public static class Builder {
        @NonNull final String title;
        @NonNull final String message;
        @Nullable String url = "";

        public Builder(@NonNull String title, @NonNull String message) {
            this.title = title;
            this.message = message;
        }

        public Builder setUrl(@Nullable String url) {
            this.url = url;
            return this;
        }

        public License build() {
            return new License(
                    this.title,
                    this.message,
                    TextUtils.isEmpty(this.url) ? "" : this.url
            );
        }
    }
}
