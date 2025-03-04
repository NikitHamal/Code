package de.raffaelhahn.coder.editor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeEditorPagerAdapter extends FragmentStateAdapter {

    private ArrayList<String> paths = new ArrayList<>();

    public CodeEditorPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CodeEditorFragment.newInstance(paths.get(position));
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }


}
