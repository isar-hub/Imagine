package com.isar.imagine.others.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.BarCodeScanningActivity
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.getTextView

class LoginActivity : AppCompatActivity() {

    lateinit var userName: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button

    val user1 = "main"
    val user2 = "billing"
    val pass = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        intialization()
        startActivity(Intent(this@LoginActivity, MainPanelFragment::class.java)).apply {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }

        loginButton.setOnClickListener {
            if (userName.text.isNotEmpty() && password.text.isNotEmpty()) {
                destination()
            } else {
                CustomDialog.showAlertDialog(
                    this@LoginActivity,
                    this@LoginActivity.getTextView("Username and Password cannot be empty"),
                    "Error"
                )

            }
        }
    }

    fun destination() {
        Log.e("Test", "destination ${userName.text}")
        if (userName.text.toString() == user1) {
            startActivity(Intent(this@LoginActivity, MainPanelFragment::class.java)).apply {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
            }
        } else if (userName.text.toString() == user2) {
            startActivity(Intent(this@LoginActivity, BarCodeScanningActivity::class.java)).apply {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
            }
        } else {
            CustomDialog.showAlertDialog(
                this@LoginActivity,
                this@LoginActivity.getTextView("Wrong Username and Password"),
                "Error"
            )

        }
    }

    private fun intialization() {
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
    }
}