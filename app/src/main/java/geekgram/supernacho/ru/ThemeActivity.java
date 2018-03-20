package geekgram.supernacho.ru;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ThemeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{
    private AppSharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new AppSharedPreferences(this);
        setTheme(prefs.getSavedTheme());
        initView();
        initNavDrawer();
        initUI();
    }

    private void initUI() {
        Button buttonThemeLime = findViewById(R.id.button_theme_0);
        buttonThemeLime.setOnClickListener(this);
        Button buttonThemeGreen = findViewById(R.id.button_theme_1);
        buttonThemeGreen.setOnClickListener(this);
        Button buttonThemeYellowBlue = findViewById(R.id.button_theme_2);
        buttonThemeYellowBlue.setOnClickListener(this);
    }

    private void initView() {
        setContentView(R.layout.activity_theme);
    }

    private void initNavDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_theme);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
        initUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationDrawerSwitch.switchIt(new WeakReference<Context>(this), item.getItemId());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
