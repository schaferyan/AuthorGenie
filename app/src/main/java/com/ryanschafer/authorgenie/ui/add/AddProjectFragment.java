package com.ryanschafer.authorgenie.ui.add;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ryanschafer.authorgenie.databinding.AddProjectFragmentBinding;
import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProjectFragment extends Fragment {

    private static final String NAME_EDITTEXT_KEY = "name edit text key";
    private static final String TARGET_EDITTEXT_KEY = "target edit text key";
    private static final String DATE_PICKER_KEY = "date picker key" ;

    AddProjectFragmentBinding binding;
    MainViewModel mViewModel;
    Goal goal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDITTEXT_KEY, binding.nameEdittext.getText().toString());
        outState.putString(TARGET_EDITTEXT_KEY, binding.targetEdittext.getText().toString());
        outState.putString(DATE_PICKER_KEY, binding.dateEdittext.getText().toString());
    }



    public AddProjectFragment() {
        // Required empty public constructor
    }

    public static AddProjectFragment newInstance() {
        return new AddProjectFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = AddProjectFragmentBinding.inflate(inflater);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button button = binding.buttonSubmitProject;
        button.setOnClickListener(buttonView -> {
            if (submitNewProject()) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//       set any saved fields
    }

    public boolean submitNewProject(){
        if (TextUtils.isEmpty(binding.nameEdittext.getText())) {
            onEmptyInput();
            return false;
        }

        try {

        }catch(NumberFormatException e){
            onInvalidInput();
            return false;
        }

        binding.nameEdittext.setText("");
        return true;
    }

    private void onEmptyInput() {
        Toast.makeText(
                requireContext(), "Projects must have a name",
                Toast.LENGTH_LONG).show();
    }


    private void onInvalidInput() {

        Toast.makeText(
                requireContext(), "Invalid input",
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}