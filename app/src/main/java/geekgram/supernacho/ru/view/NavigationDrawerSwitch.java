package geekgram.supernacho.ru.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import geekgram.supernacho.ru.R;

public final class NavigationDrawerSwitch {
    public static void switchIt(WeakReference<Context> weakContext, int id){
        Context context = weakContext.get();
        if(context != null) {
            switch (id) {
                case R.id.nav_main:
                    if (!(context instanceof MainActivity)) {
                        Intent mainIntent = new Intent(context, MainActivity.class);
                        context.startActivity(mainIntent);
                    } else {
                        Log.d("++", "its Main");
                    }
                    break;
                case R.id.nav_theme:
                    if (!(context instanceof ThemeActivity)) {
                        Intent themeIntent = new Intent(context, ThemeActivity.class);
                        context.startActivity(themeIntent);
                        if (context instanceof MainActivity) ((MainActivity) context).finish();
                    } else {
                        Log.d("++", "its Theme");
                    }
                    break;
                default:
                    Toast.makeText(context, R.string.navdrawer_menu_item_not_found, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
