package de.raffaelhahn.coder.panels;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Panel {

    @DrawableRes
    @NonNull
    private Integer icon;
    @NonNull
    private String name;
    @NonNull
    private Class<? extends Fragment> fragmentClass;
    private Fragment fragment;

    public Fragment getFragment() {
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }
}
