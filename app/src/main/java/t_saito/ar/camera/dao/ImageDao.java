package t_saito.ar.camera.dao;


import android.content.Context;

import com.github.gfx.android.orma.AccessThreadConstraint;
import com.github.gfx.android.orma.Inserter;
import com.github.gfx.android.orma.annotation.OnConflict;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import t_saito.ar.camera.BuildConfig;
import t_saito.ar.camera.model.Image;
import t_saito.ar.camera.model.OrmaDatabase;


/**
 * Created by t-saito on 2018/04/18.
 */
public class ImageDao {

    private static ImageDao IMAGE_DAO;
    private static final String DB_NAME = "main.db";
    private final OrmaDatabase ormaDatabase;

    private ImageDao() {
        this.ormaDatabase = null;
    }

    private ImageDao(Context context) {
        this.ormaDatabase = initDataBase(context);
    }

    public static ImageDao newInstance(Context context) {
        if (IMAGE_DAO == null) {
            IMAGE_DAO = new ImageDao(context);
        }
        return IMAGE_DAO;
    }

    private OrmaDatabase initDataBase(Context context) {
        OrmaDatabase orma = OrmaDatabase.builder(context)
                .name(DB_NAME)
                .readOnMainThread(AccessThreadConstraint.NONE)
                .writeOnMainThread(BuildConfig.DEBUG ? AccessThreadConstraint.WARNING : AccessThreadConstraint.NONE)
                .build();
        return orma;
    }

    public long insert(Image image) {
        Inserter<Image> statement = ormaDatabase.prepareInsertIntoImage();
        return statement.execute(image);
    }

    public long update(Image image) {
        return ormaDatabase.updateImage()
                .idEq(image.id)
                .key(image.key)
                .fileName(image.fileName)
                .filePath(image.filePath)
                .height(image.height)
                .width(image.width)
                .size(image.size)
                .mimeType(image.mimeType)
                .execute();
    }

    public Single<Long> upsert(Image image) {
        return ormaDatabase.relationOfImage()
                .inserter(OnConflict.REPLACE, false)
                .executeAsSingle(image);
    }

    public Single<List<Image>> all() {
        return Single.just(ormaDatabase.selectFromImage()
                .orderByCreatedDesc()
                .toList());
    }

    public Observable<Image> getObservable(long id) {
        return ormaDatabase.selectFromImage()
                .idEq(id)
                .executeAsObservable();
    }

    public Observable<Image> getObservable(Image image) {
        return getObservable(image.id);
    }

    public Image get(Image image) {
        return get(image.id);
    }
    public Image get(long id) {
        return ormaDatabase.selectFromImage()
                .idEq(id)
                .value();
    }

    public boolean isExist(Image image) {
        return isExist(image.id);
    }

    public boolean isExist(long id) {
        return !ormaDatabase.selectFromImage()
                .idEq(id)
                .isEmpty();
    }

    public boolean isEmpty() {
        return ormaDatabase.selectFromImage()
                .isEmpty();
    }

    public int delete(Image image) {
        return ormaDatabase.deleteFromImage()
                .idEq(image.id)
                .execute();
    }

    public Single<Image> recentGet() {
        return isEmpty() ? null : Single.just(ormaDatabase.selectFromImage()
                .orderByCreatedDesc()
                .get(0));
    }

}
