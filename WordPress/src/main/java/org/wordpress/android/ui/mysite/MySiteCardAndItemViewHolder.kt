package org.wordpress.android.ui.mysite

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

open class MySiteCardAndItemViewHolder<T : ViewBinding>(protected open val binding: T) : ViewHolder(binding.root)
