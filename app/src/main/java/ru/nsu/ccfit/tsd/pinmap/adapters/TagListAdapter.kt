package ru.nsu.ccfit.tsd.pinmap.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.TaglistItemBinding
import ru.nsu.ccfit.tsd.pinmap.fragments.PinsListFragment

class TagListAdapter(private val tags: MutableList<String>,
                     private val navController: NavController)
    : RecyclerView.Adapter<TagListAdapter.TagListHolder>() {

    private var tagList = mutableListOf<String>()

    init {
        tagList.addAll(tags)
        sortAlphabetically()
    }

    class TagListHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = TaglistItemBinding.bind(item)

        fun bind(tag: String, navController: NavController) = with(binding) {
            tagNameTextView.text = tag

            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("tag", tag)
                navController.navigate(R.id.pinsListFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.taglist_item, parent, false)
        return TagListHolder(view)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagListHolder, position: Int) {
        holder.bind(tagList[position], navController)
    }

    fun setTags(newTags: MutableList<String>) {
        tagList = newTags
        notifyDataSetChanged()
    }

    fun sortAlphabetically() {
        val comparator = Comparator<String> { tag1, tag2 -> tag1.compareTo(tag2) }
        tagList.sortWith(comparator)
        notifyDataSetChanged()
    }

    fun sortDescAlphabetically() {
        val comparator = Comparator<String> { tag1, tag2 -> tag2.compareTo(tag1) }
        tagList.sortWith(comparator)
        notifyDataSetChanged()
    }
}
