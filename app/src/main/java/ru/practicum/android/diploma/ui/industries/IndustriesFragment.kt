package ru.practicum.android.diploma.ui.industries

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.industries.ParentIndustriesAllDeal
import ru.practicum.android.diploma.presentation.industries.IndustriesViewModel

class IndustriesFragment : Fragment() {

    private val viewModel by viewModel<IndustriesViewModel>()
    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!

    private var selectedIndustries: ParentIndustriesAllDeal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefsIndustries = context?.getSharedPreferences(INDUSTRIES_PREFERENCES, Context.MODE_PRIVATE)

        val adapter = IndustriesAdapter()
        binding.regionRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.regionRecycler.adapter = adapter

        adapter.itemClickListener = { position, _ ->
            selectedIndustries = adapter.industriesList[position]
            if (selectedIndustries == null) {
                binding.industriesButtonChoose.visibility = View.GONE
            } else {
                binding.industriesButtonChoose.visibility = View.VISIBLE
            }
            adapter.setSelectedPosition(position)
        }

        binding.vacancyToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustriesState.Content -> {
                    adapter.industriesList.addAll(state.industries)
                    adapter.filteredList.addAll(state.industries)
                    adapter.notifyDataSetChanged()
                }

                is IndustriesState.Error -> ""
                is IndustriesState.Loading -> ""
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.edit.text.isNotEmpty()) {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.close_icon)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                } else {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edit.text.isNotEmpty()) {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.close_icon)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                } else {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
                if (binding.edit.text.isNotEmpty()) {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.close_icon)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                } else {
                    val newDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
                    binding.edit.setCompoundDrawablesWithIntrinsicBounds(null, null, newDrawable, null)
                }
            }
        }

        binding.industriesButtonChoose.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("keyIndustries", selectedIndustries?.name)
            setFragmentResult("requestKeyIndustries", bundle)
            sharedPrefsIndustries?.edit()?.putString(INDUSTRIES_TEXT, selectedIndustries?.name)?.apply()
            sharedPrefsIndustries?.edit()?.putString(INDUSTRIES_ID, selectedIndustries?.id)?.apply()
            findNavController().popBackStack()
        }

        binding.edit.addTextChangedListener(textWatcher)

        binding.click.setOnClickListener {
            binding.edit.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val INDUSTRIES_PREFERENCES = "industries_preferences"
        const val INDUSTRIES_TEXT = "industries_text"
        const val INDUSTRIES_ID = "industries_id"
    }
}
