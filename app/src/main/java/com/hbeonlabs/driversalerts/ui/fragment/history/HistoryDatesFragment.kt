package com.hbeonlabs.driversalerts.ui.fragment.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.timeInMillsToDate
import java.util.Calendar
import java.util.Date

class HistoryDatesFragment : BaseFragment<FragmentHistoryBinding>(),HistoryDatesAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: HistoryDatesAdapter
    val itemDecoration = RecyclerViewItemDecoration(16,50)

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_history_time
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_dates, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_dates)
        itemAdapter = HistoryDatesAdapter(getItems(),object : HistoryDatesAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {

                val navController = NavHostFragment.findNavController(requireParentFragment())

                //       val bundle = Bundle()
/*                val passList: MutableList<String> = ArrayList()
                passList.add(itemAdapter[position].uptext)
                passList.add("item 2")*/

//                bundle.putString("key","value")

                navController.navigate(R.id.historyTimeFragment);
            }
        })
        recyclerView.addItemDecoration(itemDecoration)


        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
        return view
    }

    private fun getItems(): List<ItemD> {
        val calendar: Calendar = Calendar.getInstance()
        val dateList = mutableListOf<ItemD>()
        for(i in 1..7) {
            calendar.add(Calendar.DAY_OF_YEAR, - 1)
            dateList.add(ItemD(calendar.timeInMillis.timeInMillsToDate()))
        }
        return dateList
    }

    override fun onItemClick(position: Int) {

    }

}