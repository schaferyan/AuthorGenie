package com.ryanschafer.authorgenie.statistics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.LineChart;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.FragmentGraphBinding;
import com.ryanschafer.authorgenie.goal_data.Goal;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GraphFragment extends Fragment {
    private FragmentGraphBinding binding;
    private MainViewModel mViewModel;
    private LineChart mChart;


    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGraphBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mChart = binding.chart;
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        mChart.setBorderColor(white);
        mChart.setNoDataText("No goals or progress to show. Set goals and update your word or minute count" +
                "from the main dashboard to see your progress over time");
        mChart.setNoDataTextColor(white);
        mChart.setDescription(null);
        setUpSpinners();
        setUpGraph();

        handleInsets();

    }

    private void handleInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(mChart, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() |
                    WindowInsetsCompat.Type.systemGestures());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom + 16;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setUpGraph() {
        mViewModel.getAllGoals().observe(getViewLifecycleOwner(), this::populateGraph);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1); // minimum axis-step (interval) is 1
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        mChart.getAxisRight().setEnabled(false);
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date((long) value));
            }
        };
        xAxis.setValueFormatter(formatter);
        mChart.invalidate();
    }

    private void setUpSpinners() {
        ArrayAdapter<String> durAdapter = new ArrayAdapter<>(getContext(),
                R.layout.add_goal_spinner, Goal.getDurations());
        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalDurationSpinner = binding.durationSpinner2;
        goalDurationSpinner.setAdapter(durAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                R.layout.add_goal_spinner, Goal.getGoalTypes());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalTypeSpinner = binding.typeSpinner2;

        goalTypeSpinner.setAdapter(typeAdapter);
        goalTypeSpinner.setOnItemSelectedListener(itemSelectedListener);
        goalDurationSpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private LineData getLineData(List<Goal> goals, int goalTypeId) {

        List<Goal> relevantGoals = new ArrayList<>();
        for(Goal goal : goals){
            if(goal.getGoalTypeId() == goalTypeId){
                relevantGoals.add(goal);
            }
        }

        ArrayList<Entry> goalEntries = new ArrayList<>();
        ArrayList<Entry> progEntries = new ArrayList<>();

        for(Goal goal :relevantGoals){
            Entry entry = new Entry(goal.getDeadline(), goal.getObjective());
            goalEntries.add(entry);
            Entry entry2 = new Entry(goal.getDeadline(), goal.getProgress());
            progEntries.add(entry2);
        }

        LineDataSet goalsLineDataSet = new LineDataSet(goalEntries, "Your Goals");
        goalsLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        int purple = ContextCompat.getColor(requireContext(), R.color.purple_light);
        int green = ContextCompat.getColor(requireContext(), R.color.bright_green);
        goalsLineDataSet.setColor(purple);
        goalsLineDataSet.setValueTextColor(purple);
        goalsLineDataSet.setCircleColor(purple);

        LineDataSet progressLineDataSet = new LineDataSet(progEntries, "Your Progress");
        progressLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        progressLineDataSet.setColor(green);
        progressLineDataSet.setValueTextColor(green);
        progressLineDataSet.setCircleColor(green);

        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(progressLineDataSet);
        lineDataSets.add(goalsLineDataSet);
        return new LineData(lineDataSets);
    }

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            populateGraph(mViewModel.getCachedGoals());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void populateGraph(List<Goal> goals) {
        mViewModel.setCachedGoals(goals);
        int typeId = Goal.getGoalTypeId(binding.typeSpinner2.getSelectedItemPosition(),
                binding.durationSpinner2.getSelectedItemPosition());

        mChart.setData(getLineData(goals, typeId));
        mChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        mChart.invalidate();
    }
}