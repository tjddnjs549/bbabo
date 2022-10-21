package com.evcharger.evcharger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evcharger.evcharger.databinding.ChargeritemBinding



class ChargerAdapter: ListAdapter<Data, ChargerAdapter.ChargerViewHolder>(ChargerCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargerViewHolder {
		val binding = ChargeritemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ChargerViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ChargerViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	class ChargerViewHolder(private val binding: ChargeritemBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(item: Data) {
			with(binding) {
				chargerAdd.text = item.addr
				chargerName.text = item.csNm
				chargerTp.text = item.cpTp
			}
		}
	}

	companion object {
		private val ChargerCallback = object : DiffUtil.ItemCallback<Data>() {

			override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
				return oldItem.hashCode() == newItem.hashCode()
			}

			override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
				return oldItem == newItem
			}
		}
	}

}