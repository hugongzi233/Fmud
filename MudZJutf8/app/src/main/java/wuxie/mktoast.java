package wuxie;

import android.content.Context;
import android.text.Html;
import android.widget.Toast;

public class mktoast {
    public static void huluxia(Context d) {
        Toast.makeText(d, Html.fromHtml("<font color='#7E69E3'>仙侠修真欢迎游玩</font>",Html.FROM_HTML_MODE_COMPACT), Toast.LENGTH_LONG).show();
    }
}