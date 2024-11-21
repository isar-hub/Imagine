package com.isar.imagine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils
import com.isar.imagine.Fragments.RetailerViewModelFactory
import com.isar.imagine.databinding.ActivityUserDetailsBinding
import com.isar.imagine.responses.UserDetails
import com.isar.imagine.responses.UserRole
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import com.isar.imagine.viewmodels.RetailerFragmentViewHolder
import com.isar.imagine.viewmodels.RetailerRepository
import com.isar.imagine.viewmodels.UserDatabase


class UserDetailsActivity : Fragment() {

    private var _binding: ActivityUserDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedRole: UserRole

    private val repository: RetailerRepository by lazy {
        RetailerRepository(
            FirebaseAuth.getInstance(), UserDatabase.getDatabase(requireContext()).userDao()
        )
    }

    private val viewModel: RetailerFragmentViewHolder by viewModels {
        RetailerViewModelFactory(repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityUserDetailsBinding.inflate(inflater, container, false)

        val data = arguments?.getSerializable("userDetails")
        if (data is UserDetails) {
            setUpUserDetails(data)
        }

        return binding.root
    }

    private fun setUpUserDetails(user: UserDetails) {
        binding.fullName.setText(user.name)
        binding.email.setText(user.email)
        binding.mobile.setText(user.mobile)
        binding.companyName.setText(user.companyName)
        binding.gstNumber.setText(user.gstNumber)
        binding.username.setText(user.userName)
        binding.password.setText(user.password)
        binding.confirmPassword.setText(user.password)
        binding.address.setText(user.address)
        binding.city.setText(user.city)
        binding.state.setText(user.state)
        binding.pincode.setText(user.pinCode.toString())
        binding.role.setSelection(user.role.ordinal)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        state()
        binding.fullName.requestFocus()
        binding.saveDetails.setOnClickListener {
            binding.saveDetails.isEnabled = false
            val userDetails = UserDetails(
                    uid = "",
                    name = binding.fullName.text.toString(),
                    email = binding.email.text.toString(),
                    mobile = binding.mobile.text.toString(),
                    companyName = binding.companyName.text.toString(),
                    gstNumber = binding.gstNumber.text.toString(),
                    userName = binding.username.text.toString(),
                    password = binding.confirmPassword.text.toString(),
                    address = binding.address.text.toString(),
                    city = binding.city.text.toString(),
                    state = binding.state.text.toString(),
                    pinCode = binding.pincode.text.toString().toIntOrNull() ?: 0,
                    role = UserRole.entries[binding.role.selectedItemPosition],
                    image = ""
                    )

            if (validation()) {
                viewModel.createUser(
                    binding.username.text.toString(),
                    binding.confirmPassword.text.toString(),
                    userDetails
                )
            }
        }

        observers()
        role()
    }

    private fun role() {
        val roleNames = UserRole.entries.map { it.displayName }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roleNames)
        binding.role.adapter = adapter


    }

    private fun observers() {
        viewModel.userCreated.observe(viewLifecycleOwner) { retailer ->
            when (retailer) {
                is Results.Error -> CustomDialog.showAlertDialog(
                    requireContext(), requireContext().getTextView(retailer.message!!), "Error"
                )

                is Results.Loading -> CustomProgressBar.show(requireContext(), "Loading Retailers")
                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    val result = Bundle()
                    result.putBoolean("refresh", true)
                    parentFragment?.setFragmentResult("refreshKey", result)
                    Navigation.findNavController(binding.root).navigateUp()
                }

                else -> {
                    CustomDialog.showAlertDialog(
                        requireContext(), requireContext().getTextView(retailer.message!!), "Error"
                    )
                }
            }
        }

    }


    private fun state() {

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            requireContext().resources.getStringArray(
                R.array.State
            )
        )
        binding.state.setAdapter(adapter)
    }

    private fun validation(): Boolean {
        if (binding.fullName.isEmpty()) return false
        if (binding.email.isEmpty() || !binding.email.isEmail()) return false
        if (binding.mobile.isEmpty() || !binding.mobile.isValidMobile()) return false
        if (binding.username.isEmpty()) return false
        if (binding.password.isEmpty() || !binding.password.isValidPassword()) return false
        if (binding.confirmPassword.isEmpty() || !binding.confirmPassword.isValidPassword()) return false
        if (binding.password.text.toString() != binding.confirmPassword.text.toString()) {
            binding.confirmPassword.error = "Passwords do not match"
            return false
        }
        val selectedRolePosition = binding.role.selectedItemPosition
        if (selectedRolePosition == AdapterView.INVALID_POSITION) {
            return false
        }
        if (!binding.state.isValidState()) return false
        if (binding.address.isEmpty()) return false
        if (binding.city.isEmpty()) return false
        if (binding.state.isEmpty()) return false


        return true
    }

    private fun AutoCompleteTextView.isValidState(): Boolean {
        val stateList = resources.getStringArray(R.array.State)
        if (!stateList.contains(binding.state.text.toString().trim())) {
            binding.state.error = "Invalid State"
            return false
        }
        return true
    }

    private fun EditText.isEmpty(): Boolean {
        if (TextUtils.isEmpty(this.text.toString().trim())) {
            this.error = "${this.hint} Cannot be Empty"
            return true
        }
        return false
    }

    private fun EditText.isEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!(this.text.toString().trim().matches(emailPattern.toRegex()))) {
            this.error = "Invalid Email"
            return false
        }
        return true
    }

    private fun EditText.isValidPassword(): Boolean {
        if (!(this.text.toString().trim().length >= 8)) {
            this.error = "Password should be at least 8 characters long"
            return false
        }
        return true
    }

    private fun EditText.isValidMobile(): Boolean {
        if (!this.text.toString().trim().matches("\\d{10}".toRegex())) {
            this.error = "Invalid mobile number"
            return false
        }
        return true
    }


}




