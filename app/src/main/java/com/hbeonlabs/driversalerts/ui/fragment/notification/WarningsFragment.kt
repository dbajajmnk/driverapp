package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWarningsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment

class WarningsFragment : BaseFragment<FragmentWarningsBinding>(),NotificationAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: NotificationAdapter

    override fun initView() {
        super.initView()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_warnings

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_warnings, container, false)


        //val navHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        //val navController = navHostFragment.navController

        recyclerView = view.findViewById(R.id.recyclerview)
        itemAdapter = NotificationAdapter(getItems(), object : NotificationAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                /*val childFragment = NotificationDisplay()
                val fragmentManager = requireActivity().supportFragmentManager
                val currentFragment = fragmentManager.findFragmentById(R.id.navHostFragment)

                if (currentFragment != null) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.navHostFragment, childFragment)
                        .commit()

                    Log.d("Fragment", currentFragment.toString() )
                }*/

/*                val action = MyFragmentDirections.actionMyFragmentToOtherFragment()
                findNavController().navigate(action)*/
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        return view
    }

    private fun getItems(): List<Item> {
        return listOf(
            Item("Bus Driver",R.drawable.image_person,"Item 1","Speed is over 65 near to metro station saket | Location", "17-02-2-23 at 3:54 PM"),
            Item("Bus Driver",R.drawable.image_person,"Item 2","Speed is over 65 near to metro station saket | Location","17-02-2-23 at 3:54 PM"),
        )
    }

    override fun onItemClick(position: Int) {

    }
}