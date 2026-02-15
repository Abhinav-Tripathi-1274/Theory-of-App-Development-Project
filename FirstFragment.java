package com.ap.sutra;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.ap.sutra.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StatusViewModel viewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);

        viewModel.getStatus().observe(getViewLifecycleOwner(), statusText -> {
            binding.textviewFirst.setText(statusText);

            // Update UI Colors and Progress based on AI feedback
            if (statusText.contains("DISTRACTED")) {
                binding.progressIndicator.setIndicatorColor(Color.parseColor("#FF5252")); // Red
                binding.progressIndicator.setProgress(100, true);
            } else if (statusText.contains("STUDYING") || statusText.contains("FOCUSED")) {
                binding.progressIndicator.setIndicatorColor(Color.parseColor("#4CAF50")); // Green
                binding.progressIndicator.setProgress(100, true);
            } else {
                // Scanning effect when just monitoring
                binding.progressIndicator.setIndeterminate(true);
            }
        });

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}