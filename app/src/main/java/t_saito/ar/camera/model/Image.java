package t_saito.ar.camera.model;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Setter;
import com.github.gfx.android.orma.annotation.Table;

import java.io.Serializable;

import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.util.FileUtil;
import timber.log.Timber;

/**
 * Created by t-saito on 2018/04/18.
 */
@Table
public class Image implements Serializable, Cloneable {

    @PrimaryKey(autoincrement = true)
    public final long id;
    @Column(unique = true)
    public final String key;
    @Column @Nullable
    public final String uri;
    @Column
    public final String filePath;
    @Column
    public final String fileName;
    @Column
    public final String mimeType;
    @Column
    public final int width;
    @Column
    public final int height;
    @Column
    public final long size;
    @Column(indexed = true)
    public final long created;

    public enum Type {
        JPEG,
        PNG,
    }

    @Setter
    public Image(long id, String key, @Nullable String uri, String filePath, String fileName, String mimeType, int width, int height, long size, long created) {
        this.id = id;
        this.key = key;
        this.uri = uri;
        this.filePath = filePath;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
        this.created = created;
    }

    public static class Builder {

        @NonNull private final String key;
        @Nullable private final String uri;
        @NonNull private final String filePath;
        @NonNull private final String fileName;
        private String mimeType = "unknown";
        private int width = 0;
        private int height = 0;
        private long size = 0;
        private long created = 0;

        public Builder(@NonNull String key, @Nullable String uri, @NonNull String filePath, @NonNull String fileName) {
            this.key = key;
            this.uri = uri;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public Builder setMimeType(@NonNull String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setSize(long size) {
            this.size = size;
            return this;
        }

        public Builder setCreated(long created) {
            this.created = created;
            return this;
        }

        public Image build() {
            return new Image(
                    -1,
                    this.key,
                    this.uri,
                    this.filePath,
                    this.fileName,
                    TextUtils.isEmpty(this.mimeType) ? "unknown" : this.mimeType,
                    this.width,
                    this.height,
                    this.size,
                    this.created
            );
        }
    }

    @Override
    protected Image clone() throws CloneNotSupportedException {
        Image image = null;
        try {
            image = (Image) super.clone();
        }catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }

    public static Image save(Context context, Bitmap resource) {
        long created = System.currentTimeMillis();
        String key = "img_" + Long.toHexString(created);
        String fileName = key + ".jpg";
        return save(context, resource, key, fileName, created);
    }

    /**
     * 画像保存処理
     *
     * @param context
     * @param resource
     * @param key
     * @param fileName
     * @param created
     * @return
     */
    public static Image save(Context context, Bitmap resource, String key, String fileName, long created) {
        // 画像をローカルディレクトリに保存
        String path = FileUtil.saveImage(resource, fileName);
        if (TextUtils.isEmpty(path)) return null;
        // 画像をMediaStoreに保存
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATA, path);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Image image = new Image.Builder(key, String.valueOf(uri), path, fileName)
                .setMimeType(FileUtil.getMIMEType(path))
                .setWidth(resource.getWidth())
                .setHeight(resource.getHeight())
                .setSize(resource.getByteCount())
                .setCreated(created)
                .build();
        if (image == null) return null;
        // 画像をDBに保存
        ImageDao imageDao = ImageDao.newInstance(context);
        long rowId = imageDao.isExist(image) ? imageDao.update(image) : imageDao.insert(image);
        Timber.d("rowId %d\npath %s", rowId ,image.filePath);
        return imageDao.get(rowId);
    }

    /**
     * 画像削除処理
     *
     * @param context
     * @param image
     * @return
     */
    public static boolean delete(@NonNull Context context, @NonNull Image image) {
        boolean result = FileUtil.deleteImage(context, image);
        try {
            Uri uri = Uri.parse(image.uri);
            if (result && uri != null) {
                int cnt = context.getContentResolver().delete(uri, null, null);
                Timber.d("Deleted target image :%d", cnt);
            }
        } catch (Exception e) {
            Timber.w(e);
        }
        Timber.w("%s to deleted image ", result ? "Success" : "Failed");
        return result;
    }
}
