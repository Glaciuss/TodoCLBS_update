package com.example.todo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle:ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            val intent = Intent(this@MainActivity, AboutUs::class.java)
            when(it.itemId){
                R.id.item -> startActivity(intent)
                R.id.contact -> sendEmail()
                else -> startActivity(intent)
            }
            true
        }
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        val arr = arrayOf("ontninjao@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, arr)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback TODO App")

        try{
            startActivity(Intent.createChooser(intent,"Choose Email Client"))
        }catch (o:Exception){
            Toast.makeText(this,"someting went wrong",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navigate = findNavController(R.id.fragment)
        return super.onSupportNavigateUp()|| navigate.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}