package com.nidhi.demoproject;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nidhi.demoproject.fragments.CameraFragment;
import com.nidhi.demoproject.fragments.GalleryFragment;
import com.nidhi.demoproject.fragments.SampleFragment;
import com.nidhi.demoproject.fragments.SlideshowFragment;
import com.nidhi.demoproject.fragments.ToolsFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = HomeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final TextView textView = (TextView)toolbar.findViewById(R.id.tv_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Toolbar Title event",Toast.LENGTH_SHORT).show();
                textView.setText(getString(R.string.app_name));
            }
        });

        ImageView imageView = (ImageView)toolbar.findViewById(R.id.iv_search);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Toolbar Search Icon Event",Toast.LENGTH_SHORT).show();
            }
        });

        //setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Snackbar message",Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"OK",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView imageView_header = (ImageView)headerView.findViewById(R.id.imageView_header);
        imageView_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Header Image (Profile Image event)",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_camera:
                //load camera fragment.
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new CameraFragment()).commit();
                break;
            case R.id.nav_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new GalleryFragment()).commit();
                break;
            case R.id.nav_slideshow:
                // inject Slide show fragment.
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SlideshowFragment()).commit();
                break;
            case R.id.nav_manage:
                // inject Tools fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ToolsFragment()).commit();
                break;
            case R.id.itemId:
                //inject sample fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SampleFragment()).commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
