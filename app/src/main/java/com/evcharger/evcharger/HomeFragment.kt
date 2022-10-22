package com.evcharger.evcharger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evcharger.evcharger.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

	private var param1: String? = null
	private var param2: String? = null
	private val listItems = arrayListOf<ListLayout>()
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

		binding.mainSearchBtn.setOnClickListener {
			retrofitWork()
		}

		//여기다 채우기
		return view
	}
	private fun retrofitWork() {

		val service = RetrofitApi.chargerService

		service.getChargerData(getString(R.string.charger_key), "json")
			.enqueue(object : Callback<chargerResponse> {
				override fun onResponse(
					call: Call<chargerResponse>,
					response: Response<chargerResponse>
				) { addItemsAndMarkers(response.body()) }
				override fun onFailure(call: Call<chargerResponse>, t: Throwable) {
					Log.d("TAG",t.message.toString())
				}
			})
	}
	private fun addItemsAndMarkers(searchResult: chargerResponse?) {

		for (document in searchResult!!.data!!) {
			// 결과를 리사이클러 뷰에 추가
			val item = ListLayout(
				document?.addr,
				document?.cpNm,
				document?.cpTp
			)
			listItems.add(item)

		}
	}
	
	//정적 메모리
	companion object {

		@JvmStatic
		fun newInstance() :HomeFragment {
			return HomeFragment()
		}
	}
}