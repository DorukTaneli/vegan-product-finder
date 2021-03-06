package com.example.mcateam6.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.mcateam6.R
import com.example.mcateam6.activities.AddProductFormPage
import com.example.mcateam6.database.RemoteDatabase


class ProductGeneralInformationFragment : AddProductFormPageAsyncFragment() {

    override val formPage = AddProductFormPage.GENERAL_INFORMATION

    private var enNameShowError = false
    private var krNameShowError = false

    private var enNameValid = false
    private var krNameValid = false

    private lateinit var enNameEdit: EditText
    private lateinit var krNameEdit: EditText
    private lateinit var brandEdit: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_product_general_information, container, false)

        findViews(v)

        loadProductModel()

        initShowError()

        initValidation()

        updateValidationModel()

        addTextChangedListeners()

        return v
    }

    private fun initShowError() {
        enNameShowError = productModel.englishName.isNotBlank()
        krNameShowError = productModel.koreanName.isNotBlank()
    }

    private fun initValidation() {
        enNameValid = productModel.englishName.isNotBlank()
        krNameValid = productModel.koreanName.isNotBlank()
    }

    private fun loadProductModel() {
        enNameEdit.setText(productModel.englishName)
        krNameEdit.setText(productModel.koreanName)
        brandEdit.setText(productModel.brand)
    }

    private fun findViews(v: View) {
        enNameEdit = v.findViewById(R.id.en_name_edit)
        krNameEdit = v.findViewById(R.id.kr_name_edit)
        brandEdit = v.findViewById(R.id.brand_edit)
    }

    private fun addTextChangedListeners() {
        enNameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                enNameShowError = true

                enNameValid = enNameEdit.text.toString().isNotBlank()
                enNameEdit.error =
                    if (enNameValid) {
                        null
                    } else {
                        getString(R.string.error_required_field, getString(R.string.english_name))
                    }
                updateValidationModel()

                productModel.englishName = enNameEdit.text.toString().trim()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        krNameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                krNameShowError = true

                krNameValid = krNameEdit.text.toString().isNotBlank()
                krNameEdit.error =
                    if (krNameValid) {
                        null
                    } else {
                        getString(R.string.error_required_field, getString(R.string.korean_name))
                    }
                updateValidationModel()

                productModel.koreanName = krNameEdit.text.toString().trim()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        brandEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productModel.brand = brandEdit.text.toString().trim()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateValidationModel() {
        pagedFormModel.setIsValid(
            formPage,
            enNameValid && krNameValid
        )
    }

    override fun asyncValidation(cont: (Boolean) -> Unit) {
        val db = RemoteDatabase()

        db.signIn().addOnSuccessListener {
            db.exists(productModel.brand, productModel.englishName).addOnSuccessListener { exists ->
                if (exists) {
                    if (productModel.brand.isBlank()) {
                        Toast.makeText(activity, "A product with this name already exists!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "A product with the same name and brand already exists!", Toast.LENGTH_SHORT).show()
                    }
                }

                // The product is valid if it does not yet exist
                cont(!exists)
            }
        }
    }
}
