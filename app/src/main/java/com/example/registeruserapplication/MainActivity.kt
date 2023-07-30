package com.example.registeruserapplication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registeruserapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    // Write a message to the database
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference.child("MyUsers")

    val userList = ArrayList<Users>()
    lateinit var usersAdapter: UsersAdapter
    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    val storageReference: StorageReference = firebaseStorage.reference

    val userImgList = ArrayList<String>()
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

                // delete image
                val userImageName = usersAdapter.getUserImgName(viewHolder.adapterPosition)

                val imageReference = storageReference.child("images").child(userImageName)
                imageReference.delete()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAllUsers) {
            showDialogMessge()
        }else if (item.itemId == R.id.signout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialogMessge() {
        val dialogMessage = AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All Users")
        dialogMessage.setMessage("If click Yes, all users will delete," + "If you want to delete a specific user, you can swipe the item you want to delete right or left")
        dialogMessage.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })

        dialogMessage.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->

            dbRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (eachuser in snapshot.children) {
                        val user = eachuser.getValue(Users::class.java)
                        if(user != null) {

                            userImgList.add(user.imageName)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })



            dbRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    for (imageName in userImgList) {
                        val imageReference = storageReference.child("images").child(imageName)
                        imageReference.delete()
                    }

                    usersAdapter.notifyDataSetChanged()

                    Toast.makeText(applicationContext, "All users were deleted", Toast.LENGTH_SHORT).show()
                }
            }
        })

        dialogMessage.create().show()
    }
}