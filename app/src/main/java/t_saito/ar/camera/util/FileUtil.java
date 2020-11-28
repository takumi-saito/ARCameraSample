package t_saito.ar.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import t_saito.ar.camera.BuildConfig;
import t_saito.ar.camera.dao.ImageDao;
import t_saito.ar.camera.model.Image;
import timber.log.Timber;

public class FileUtil {

    public static final String ROOT_DIR = "ARCameraImage";

    /**
     * ボリュームが利用可能であるか確認 読み書き
     *  (現在ストレージ領域を他の何かが処理をしていてアクセスすべきでない場合かどうかを確認)
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 読み取り、書き込み可能
            return true;
        }
        return false;
    }

    /**
     * ボリュームが利用可能であるか確認 読みのみ
     * (現在ストレージ領域を他の何かが処理をしていてアクセスすべきでない場合かどうかを確認)
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                // 読み取り可能
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getImageRootDir() {
        // assetsの音源を外部SDカードにコピー
        StringBuilder dist = new StringBuilder();
        dist.append(Environment.getExternalStorageDirectory());
        dist.append("/");
        dist.append(ROOT_DIR);
        return dist.toString();
    }

    /**
     * Bitmapをjpg形式で保存
     * @param bitmap
     * @param fileName
     */
    public static String saveImage(@NonNull Bitmap bitmap, @NonNull String fileName) {
        boolean result = true;
        File imageFile = new File(getImageRootDir(), fileName);
        // 親ファイルも作成
        if (!imageFile.getParentFile().exists()) {
            result = imageFile.getParentFile().mkdirs();
        }
        if (!result) return null;
        String saveImagePath = imageFile.getAbsolutePath();
        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveImagePath;
    }

    /**
     * ファイルパスからMIMETypeを取得
     * @param Path
     * @return
     */
    public static String getMIMEType(String Path){
        //ファイルから拡張子取得
        File file = new File(Path);
        String fn = file.getName();
        int ch = fn.lastIndexOf('.');
        String ext = (ch>=0)?fn.substring(ch + 1):null;

        //拡張子からMIMEType取得
        String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
        return MIME;
    }

    public static boolean deleteImage(@NonNull Context context, @NonNull Image image) {
        // 画像がDBにあるかチェック
        ImageDao imageDao = ImageDao.newInstance(context);
        if (!imageDao.isExist(image)) {
            // レコードが存在しない場合
            return false;
        }
        int rowId = imageDao.delete(image);
        if (rowId < 0) {
            // レコードの削除に失敗した場合
            return false;
        }
        File f = new File(image.filePath);
        return f.delete();
    }

    public static Uri shareImage(@NonNull Context context, @NonNull Image image) {
        try {
            File shareImg = new File(getImageRootDir(), image.fileName);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", shareImg);   // TODO authorityをどこかで定義する
            Timber.d("content uri:%s", contentUri);
            return contentUri;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }
}
