package com.ryanschafer.authorgenie.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.AddTabManagerFragmentBinding;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTabManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTabManagerFragment extends Fragment {

    private static final String ADD_GOAL_FRAGMENT_KEY = "AddGoalFragment";
    private static final String ADD_PROJECT_FRAGMENT_KEY ="AddProjectFragment" ;
    private static final String TAB_LAST_SELECTED_KEY = "tab_last_selected";
    AddTabManagerFragmentBinding binding;
    AddGoalFragment mAddGoalFragment;
    AddProjectFragment mAddProjectFragment;
    FragmentManager mFragmentManager;
    int tabLastSelected;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        restoreFragmentInstances(savedInstanceState);
        
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
        
        if (mAddGoalFragment != null && mAddGoalFragment.isAdded()) {
            try {
                mFragmentManager.putFragment(outState, ADD_GOAL_FRAGMENT_KEY, mAddGoalFragment);
            } catch (IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        if (mAddProjectFragment != null && mAddProjectFragment.isAdded()) {
            try {
                mFragmentManager.putFragment(outState, ADD_PROJECT_FRAGMENT_KEY, mAddProjectFragment);
            } catch (IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        outState.putInt(TAB_LAST_SELECTED_KEY, tabLastSelected);
    }


    public AddTabManagerFragment() {
        // Required empty public constructor
    }

    public static AddTabManagerFragment newInstance() {
            return new AddTabManagerFragment();
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = AddTabManagerFragmentBinding.inflate(inflater);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLastSelected = tab.getPosition();
                changeFragmentByReadingTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (savedInstanceState != null) {
            tabLastSelected = savedInstanceState.getInt(TAB_LAST_SELECTED_KEY, 0);
        }else{
            tabLastSelected = 0;
        }
        TabLayout.Tab currentTab = binding.tabLayout.getTabAt(tabLastSelected);
        binding.tabLayout.selectTab(currentTab);
        if (currentTab != null) {
            changeFragmentByReadingTab(currentTab);
        }
        return binding.getRoot();
    }
    public void showFragment(Fragment fragment) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView,
                fragment);
        transaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    public void changeFragmentByReadingTab(TabLayout.Tab tab){
        String tabText = tab.getText().toString();
        if(tabText.equals(getResources().getString(R.string.new_goal))){
            showAddGoalFragment();
        }
        else if(tabText.equals(getResources().getString(R.string.new_project))){
            showAddProjectFragment();
        }
    }

    private void showAddProjectFragment() {
        if (mAddProjectFragment == null){
            mAddProjectFragment = AddProjectFragment.newInstance();
        }
        showFragment(mAddProjectFragment);
    }

    private void showAddGoalFragment() {
        if (mAddGoalFragment == null){
            mAddGoalFragment = AddGoalFragment.newInstance();
        }
        showFragment(mAddGoalFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    

    private void restoreFragmentInstances(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            //Restore fragment instances
            mAddGoalFragment = (AddGoalFragment) mFragmentManager
                    .getFragment(savedInstanceState, ADD_GOAL_FRAGMENT_KEY);
            mAddProjectFragment = (AddProjectFragment) mFragmentManager
                    .getFragment(savedInstanceState, ADD_PROJECT_FRAGMENT_KEY);
            
        }
    }
    
}