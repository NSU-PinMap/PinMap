package ru.nsu.ccfit.tsd.pinmap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.TaglistItemBinding

class TagListAdapter(private val tags: MutableList<String>) : RecyclerView.Adapter<TagListAdapter.TagListHolder>() {
    private var tagList = mutableListOf<String>()

    init {
        tagList.addAll(tags)
        sortAlphabetically()
    }

    class TagListHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = TaglistItemBinding.bind(item)

        fun bind(tag: String) = with(binding) {
            tagNameTextView.text = tag
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
        holder.bind(tagList[position])
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
