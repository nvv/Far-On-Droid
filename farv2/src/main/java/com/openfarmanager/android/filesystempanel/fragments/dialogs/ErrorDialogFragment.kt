package com.openfarmanager.android.filesystempanel.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.openfarmanager.android.R
import kotlinx.android.synthetic.main.dialog_error.view.*

class ErrorDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_error, container, false).apply {

            this.cancel.setOnClickListener {
                dismiss()
            }

            this.text.text = arguments?.getString(ARG_MESSAGE)
        }
    }

    companion object {

        const val ARG_MESSAGE = "ARG_MESSAGE"

        fun newInstance(message: String) =
                ErrorDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MESSAGE, message)
                    }
                }

    }

}