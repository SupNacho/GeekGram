package geekgram.supernacho.ru.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoFragmentsAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments = new ArrayList<>();
    Map<Fragment, String> fragmentsTitle = new HashMap<>();

    public PhotoFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        this.fragments.add(fragment);
        this.fragmentsTitle.put(fragment, title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fr = fragments.get(position);
        return fragmentsTitle.get(fr);
    }
}

