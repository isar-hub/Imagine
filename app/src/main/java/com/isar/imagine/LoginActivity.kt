package com.isar.imagine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    lateinit var userName : EditText
    lateinit var password : EditText
    lateinit var loginButton : Button

    val user1 = "main"
    val user2 = "billing"
    val user3 = "retailer"
    val pass = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        intialization()

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("username", "2")
        }
        startActivity(intent)

        loginButton.setOnClickListener{
            if (userName.text.isNotEmpty() && password.text.isNotEmpty()) {
                destination()
            }
            else{
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "Please write username and password ", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        // Handle the action here
                        // e.g., retrying an operation
                    }
                    .show()
            }
        }
    }
    fun destination()
    {
        Log.e("Test","destination ${userName.text}")
        if(userName.text.toString() == user1){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", "3")
            }
            startActivity(intent)
        }
        else if(userName.text.toString() == user2){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", "2")
            }
            startActivity(intent)
        }
        else if(userName.text.toString()== user3){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", "1")
            }
            startActivity(intent)
        }
        else{
            val view = findViewById<View>(android.R.id.content)
            Snackbar.make(view, "Wrong credentials ", Snackbar.LENGTH_LONG)
                .setAction("OK") {
                    // Handle the action here
                    // e.g., retrying an operation
                }
                .show()
        }
    }
    private fun intialization() {
      userName = findViewById(R.id.userName)
      password = findViewById(R.id.password)
      loginButton = findViewById(R.id.loginButton)
    }
}