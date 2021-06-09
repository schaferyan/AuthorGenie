package com.ryanschafer.authorgenie3.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ryanschafer.authorgenie3.R;
import com.ryanschafer.authorgenie3.databinding.MainFragmentBinding;
import com.ryanschafer.authorgenie3.ui.main.recyclerview.RecyclerviewFragment;


public class MainFragment extends Fragment {

    MainFragmentBinding binding;

    public MainFragment(){}

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.add_progress_fragment_container, ProgressInputFragment.newInstance())
                    .commitNow();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.recyclerview_fragment_container, RecyclerviewFragment.newInstance())
                    .commitNow();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }
}
