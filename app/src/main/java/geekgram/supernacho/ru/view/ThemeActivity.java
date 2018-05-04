package geekgram.supernacho.ru.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import geekgram.supernacho.ru.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ThemeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppSharedPreferences prefs;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.font_name))
                .setFontAttrId(R.attr.fontPath)
                .build());
        prefs = new AppSharedPreferences(this);
        setTheme(prefs.getSavedTheme());
        initView();
        initNavDrawer();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initView() {
        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);
    }

    private void initNavDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_theme);
    }

    @OnClick({R.id.button_theme_0, R.id.button_theme_1, R.id.button_theme_2})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.button_theme_0:
                prefs.saveThemeId(R.style.ThemeStandard_Lime);
                setTheme(R.style.ThemeStandard_Lime);
                break;
            case R.id.button_theme_1:
                prefs.saveThemeId(R.style.ThemeStandard_GreenBerry);
                setTheme(R.style.ThemeStandard_GreenBerry);
                break;
            case R.id.button_theme_2:
                prefs.saveThemeId(R.style.ThemeStandard_YellowBlue);
                setTheme(R.style.ThemeStandard_YellowBlue);
                break;
            default:
                Toast.makeText(this, "No such Theme", Toast.LENGTH_LONG).show();
                break;
        }

        initView();
        initNavDrawer();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawerSwitch.switchIt(new WeakReference<>(this), item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
