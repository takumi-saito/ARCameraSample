package t_saito.ar.camera.databinding;

import android.databinding.BindingAdapter;
import android.graphics.Paint;
import android.os.Build;
import android.text.Html;
import android.widget.TextView;


/**
 * TextViewカスタムセッター
 * @author t-saito
 */
public class TextViewBinding {
    @BindingAdapter("setLinkFromUrl")
    public static void setLink(TextView textView, String url) {
        textView.setText(url);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @BindingAdapter("setTextFromHtml")
    public static void setHtml(TextView textView, String cdata) {
        textView.setText((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(cdata, Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml(cdata)));
    }
}
