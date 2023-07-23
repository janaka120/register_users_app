package com.example.registeruserapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.registeruserapplication.databinding.UserItemBinding

class UsersAdapter(var context: Context, var userList: ArrayList<Users>) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    inner class UsersViewHolder(val adapterBinding: UserItemBinding) : RecyclerView.ViewHolder(adapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UsersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.adapterBinding.textViewName.text = userList[position].userName
        holder.adapterBinding.textViewAge.text = userList[position].userAge.toString()
        holder.adapterBinding.textViewEmail.text = userList[position].userEmail
    }
}