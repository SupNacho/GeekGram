package geekgram.supernacho.ru;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentInteractionListener {
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavDrawer();
        initFragment();
    }

    private void initFragment() {
        mainFragment = MainFragment.newInstance();
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.add(R.id.main_fragment_container, mainFragment);
        fragmentManager.commit();
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

        //ДЗ пункт №3
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_enabled}};
        int[] colors = new int[]{
                Color.MAGENTA,
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE};

        navigationView.setItemTextColor(new ColorStateList(states, colors));
        navigationView.setItemIconTintList(new ColorStateList(states, colors));
        navigationView.setCheckedItem(R.id.nav_main);
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
    public void onFragmentInteraction(Uri uri) {
        // TODO: 06.03.2018 implement fragment interaction
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_main:
                Toast.makeText(this, "Main clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_theme:
                Intent themeIntent = new Intent(MainActivity.this, ThemeActivity.class);
                startActivity(themeIntent);
                break;
            default:
                Toast.makeText(this, "Menu Item not found", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
