package com.evcharger.evcharger

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evcharger.evcharger.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

	private var param1: String? = null
	private var param2: String? = null
	private val binding: FragmentHomeBinding by lazy {
		FragmentHomeBinding.inflate(layoutInflater)
	}
	private val chargerAdapter by lazy {
		ChargerAdapter()
	}
	//메모리가 올라갔을 때
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
			param2 = it.getString(ARG_PARAM2)
		}
		
	}
	//뷰가 생성됐을 때
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater!!.inflate(R.layout.fragment_home, container, false)
		val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

		binding.recyclerView.apply {
			layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
			adapter = chargerAdapter
			addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
		}

		//여기다 채우기
		return view
	}
	
	//정적 메모리
	companion object {

		@JvmStatic
		fun newInstance() :HomeFragment {
			return HomeFragment()
		}
	}
}