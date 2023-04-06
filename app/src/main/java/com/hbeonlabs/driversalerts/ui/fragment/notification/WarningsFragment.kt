package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
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

        recyclerView = view.findViewById(R.id.recyclerview)
        itemAdapter = NotificationAdapter(getItems(), object : NotificationAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {

                val navController = NavHostFragment.findNavController(requireParentFragment())

         //       val bundle = Bundle()
/*                val passList: MutableList<String> = ArrayList()
                passList.add(itemAdapter[position].uptext)
                passList.add("item 2")*/

//                bundle.putString("key","value")

                navController.navigate(R.id.notificationDisplay);
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