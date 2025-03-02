package de.raffaelhahn.coder.intro;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class IntroFragmentStateAdapter extends FragmentStateAdapter {
    public IntroFragmentStateAdapter(IntroActivity introActivity) {
        super(introActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new IntroWelcomeFragment();
            case 1 -> new IntroPermissionFragment();
            case 2 -> new IntroTerminalInstallationFragment();
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
