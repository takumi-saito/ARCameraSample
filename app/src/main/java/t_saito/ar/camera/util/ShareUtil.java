package t_saito.ar.camera.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import t_saito.ar.camera.CommonConstant;
import t_saito.ar.camera.analitycs.AnswersEventClient;
import t_saito.ar.camera.analitycs.ShareAnswers;

public class ShareUtil {

    public enum Result {
        SUCCESS,
        IMAGE_NOT_FOUND,
        APP_NOT_FOUND
    }

    public static Result shareImage(Context context, Uri uri) {
        if (uri == null) {
            return Result.IMAGE_NOT_FOUND;
        }

        String hashTag =
                CommonConstant.HASH_TAG_WIBUN + " " +
                        CommonConstant.HASH_TAG_WIBUN_PHOTO + " " +
                        CommonConstant.HASH_TAG_WBFEST;

        String chooserTitle = "共有";

        Intent baseIntent = new Intent(Intent.ACTION_SEND);
        baseIntent.putExtra(Intent.EXTRA_TEXT, hashTag);
        baseIntent.setType("image/*");
        baseIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(baseIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfoList.isEmpty()) {
            // パッケージリストが取得できない場合
            context.startActivity(Intent.createChooser(baseIntent, chooserTitle));
        } else {
            // パッケージリストが取得できる場合
            List<LabeledIntent> shareIntentList = new ArrayList<>();
            resolveInfoList.sort(new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
            resolveInfoList.forEach(resolveInfo -> {
                Intent shareIntent = new Intent(baseIntent);
                String packageName = resolveInfo.activityInfo.packageName;
                switch (packageName) {
                    case CommonConstant.PACKAGE_NAME_TWITTER:
                    case CommonConstant.PACKAGE_NAME_FACEBOOK:
                        shareIntent.putExtra(Intent.EXTRA_TEXT, hashTag);
                    case CommonConstant.PACKAGE_NAME_INSTAGRAM:
                        // フィルタするにはClassNameの指定が必要
                        shareIntent.setClassName(packageName, resolveInfo.activityInfo.name);
                        shareIntentList.add(new LabeledIntent(shareIntent, packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
                        break;
                }
            });

            if (shareIntentList.size() < 1) {
                return Result.APP_NOT_FOUND;
            }

            // リストの最後を取得しないと並び順が変になる
            Intent chooserIntent = Intent.createChooser(shareIntentList.remove(shareIntentList.size() - 1), chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentList.toArray(new LabeledIntent[shareIntentList.size()]));
            context.startActivity(chooserIntent);
        }
//        AnswersEventClient.send(new ShareAnswers.Builder().build());
        return Result.SUCCESS;
    }
}
