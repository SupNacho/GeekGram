package geekgram.supernacho.ru;


import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {

    private static final String APP_PREFS = "geek_gram_prefs";
    private static final String APP_PREFS_THEME = "geek_gram_theme";

    private Context context;
    private SharedPreferences prefs;

    public AppSharedPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public void saveThemeId(int idTheme){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_PREFS_THEME, idTheme);
        editor.apply();
    }

    public int getSavedTheme(){
        return prefs.getInt(APP_PREFS_THEME, R.style.ThemeStandard_Lime);
    }
}
