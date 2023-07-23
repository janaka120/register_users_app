package com.example.registeruserapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registeruserapplication.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    // Write a message to the database
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference.child("MyUsers")

    val userList = ArrayList<Users>()
    lateinit var usersAdapter: UsersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        mainBinding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val userId = usersAdapter.getUserId(viewHolder.adapterPosition)
                dbRef.child(userId).removeValue()

                Toast.makeText(applicationContext, "The user was deleted", Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(mainBinding.recycleView)

        retrieveDataFromDb()

    }

    fun retrieveDataFromDb() {
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (eachuser in snapshot.children) {
                    val user = eachuser.getValue(Users::class.java)
                    if(user != null) {
                        println("User Id: ${user.userId}")
                        println("User Name: ${user.userName}")
                        println("User Age: ${user.userAge}")
                        println("User Email: ${user.userEmail}")
                        println("-----------------")

                        userList.add(user)
                    }
                    usersAdapter = UsersAdapter(this@MainActivity, userList)
                    mainBinding.recycleView.layoutManager = LinearLayoutManager(this@MainActivity)
                    mainBinding.recycleView.adapter = usersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}