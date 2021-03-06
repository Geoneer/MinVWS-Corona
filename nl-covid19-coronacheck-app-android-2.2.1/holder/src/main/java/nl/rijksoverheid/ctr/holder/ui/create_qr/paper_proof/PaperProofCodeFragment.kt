package nl.rijksoverheid.ctr.holder.ui.create_qr.paper_proof

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import nl.rijksoverheid.ctr.holder.R
import nl.rijksoverheid.ctr.holder.databinding.FragmentPaperProofCodeBinding
import nl.rijksoverheid.ctr.holder.ui.create_qr.models.PaperProofCodeResult
import nl.rijksoverheid.ctr.shared.ext.hideKeyboard
import nl.rijksoverheid.ctr.shared.ext.navigateSafety
import nl.rijksoverheid.ctr.shared.ext.showKeyboard
import nl.rijksoverheid.ctr.shared.livedata.EventObserver
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.scope.emptyState

class PaperProofCodeFragment : Fragment(R.layout.fragment_paper_proof_code) {

    private val viewModel: PaperProofCodeViewModel by stateViewModel(
        state = emptyState(),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPaperProofCodeBinding.bind(view)
        showKeyboard(binding.codeInputText)

        binding.codeInputText.addTextChangedListener {
            viewModel.code = it?.toString() ?: ""
        }

        viewModel.codeResultLiveData.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is PaperProofCodeResult.None -> {

                }
                is PaperProofCodeResult.Valid -> {
                    navigateSafety(R.id.nav_paper_proof_code, PaperProofCodeFragmentDirections.actionPaperProofConsent(
                        binding.codeInputText.text.toString()
                    ))
                }
                is PaperProofCodeResult.NotSixCharacters -> {
                    binding.codeInput.error =
                        getString(R.string.add_paper_proof_input_not_6_chars)
                }
                is PaperProofCodeResult.Invalid -> {
                    binding.codeInput.error =
                        getString(R.string.add_paper_proof_input_invalid)
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.bottom.setButtonEnabled(it.buttonEnabled)
        }

        binding.noLetterCombinationBtn.setOnClickListener {
            navigateSafety(PaperProofCodeFragmentDirections.actionTitleDescriptionBottomSheet(
                title = getString(R.string.no_letter_combination_dialog_title),
                description = getString(R.string.no_letter_combination_dialog_description)
            ))
        }

        binding.bottom.setButtonClick {
            binding.codeInput.error = null
            viewModel.validateCode()
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }
}