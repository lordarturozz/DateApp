package com.example.dateapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.dateapp.adapter.DatingAdapter
import com.example.dateapp.databinding.FragmentDatingBinding
import com.example.dateapp.model.UserModel
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction


class DatingFragment : Fragment() {
    private lateinit var binding : FragmentDatingBinding
    private lateinit var manager : CardStackLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDatingBinding.inflate(layoutInflater)

        getData()
        return binding.root
    }

    private fun init() {
        manager = CardStackLayoutManager(requireContext(),object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {
                TODO("Not yet implemented")
            }

            override fun onCardSwiped(direction: Direction?) {
                if(manager.topPosition == list.size ){
                    Toast.makeText(requireContext(),"this is the last card",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {
                TODO("Not yet implemented")
            }

            override fun onCardCanceled() {
                TODO("Not yet implemented")
            }

            override fun onCardAppeared(view: View?, position: Int) {
                TODO("Not yet implemented")
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        })
        manager.setVisibleCount(3)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
    }
    private lateinit var list : ArrayList<UserModel>
    private fun getData() {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    Log.d("Arturo", "onDataChange: ${snapshot.toString()}")
                    if (snapshot.exists()) {
                        list = arrayListOf()
                        for (data in snapshot.children) {
                            val model = data.getValue(UserModel::class.java)
                            list.add(model!!)
                        }
                        list.shuffle()
                        init()
                        binding.cardStackView.layoutManager = manager
                        binding.cardStackView.itemAnimator = DefaultItemAnimator()
                        binding.cardStackView.adapter = DatingAdapter(requireContext(), list)
                    } else {
                        Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}