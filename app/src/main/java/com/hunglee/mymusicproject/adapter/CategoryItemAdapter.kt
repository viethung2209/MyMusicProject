package com.hunglee.mymusicproject.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hunglee.mymusicproject.R
import com.hunglee.mymusicproject.databinding.CatRowItemBinding
import com.hunglee.mymusicproject.interfaces.ICategoryItemClickListener
import com.hunglee.mymusicproject.media.MediaManager
import com.hunglee.mymusicproject.model.Song

class CategoryItemAdapter(
    private val context: Context,
    private val categoryItem: List<Song>,
    private val onPlayMusic: ICategoryItemClickListener
) : RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder>() {

    var data: ByteArray? = null
    private var arrSong = ArrayList<Song>()
    var isPushed: Boolean = false

    class CategoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//        , View.OnClickListener
    {
        var itemImage: ImageView
        var itemTitle: TextView
        var itemCategory: CardView
//        var iCategoryItem: ICategoryItemClickListener? = null

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.tv_title)
            itemCategory = itemView.findViewById(R.id.cv_cat_item_row)
//            itemView.setOnClickListener(this)


        }


//        fun subCategoryInterfaceClick(iCategoryItemInterface: ICategoryItemClickListener) {
//            this.iCategoryItem = iCategoryItemInterface
//        }

//        override fun onClick(p0: View?) {
//            iCategoryItem!!.onClickCategoryItem(p0!!, false)
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        pushDataOffline()



        CatRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cat_row_item, parent, false)
        )


    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {

        holder.itemTitle.text = categoryItem[position].title
        if (categoryItem[position].path == null) {
            Glide.with(holder.itemImage)
                .load(categoryItem[position].thumbnail)
                .error(R.drawable.ic_music)
                .placeholder(R.drawable.ic_music)
                .into(holder.itemImage)

        } else {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(categoryItem[position].path)
            data = mmr.embeddedPicture
            if (data != null) {
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                holder.itemImage.setImageBitmap(bitmap)
            } else {
                holder.itemImage.setImageResource(R.drawable.ic_music)
            }
            holder.itemImage.adjustViewBounds = false
            holder.itemImage.layoutParams = LinearLayout.LayoutParams(500, 400)
        }

        holder.itemCategory.setOnClickListener {
            onPlayMusic.playSong(arrSong, position)
        }
//        holder.subCategoryInterfaceClick(
//            object : ICategoryItemClickListener {
//            override fun onClickCategoryItem(view: View, isLongPress: Boolean) {
//                val intent = Intent(context, TrackActivity::class.java)
//                val bundle = Bundle()
//                bundle.putSerializable(
//                    "category_item",
//                    categoryItem[holder.bindingAdapterPosition]
//                )
//                bundle.putSerializable("image_item", data)
//                bundle.putInt("item_position", position)
//                intent.putExtras(bundle)
////                intent.putExtra("cat_image", categoryItem[holder.bindingAdapterPosition].imageUrl)
////                intent.putExtra("cat_artist", categoryItem[holder.bindingAdapterPosition].title)
////                Log.d("doanpt", "Tile is ${categoryItem[holder.bindingAdapterPosition].title}")
////                Log.d("doanpt", "Position is $position")
//                context.startActivity(intent)
//
//            }

//            object : ICategoryItemClickListener {
//                override fun playSong(araList: List<Song>, position: Int) {
//
//                }
//            }
//        )
    }


    override fun getItemCount(): Int {
        return categoryItem.size
    }

    private fun pushDataOffline() {
        if (!isPushed) {
            arrSong = MediaManager.getMySongList()
            Log.d("doanpt", "Data pushed: ${arrSong.size}")
            isPushed = true
        } else
            return
    }

    private fun clearArrSongOffline() {
        if (isPushed) {
            arrSong = ArrayList()
            Log.d("doanpt", "Data cleared: ${arrSong.size}")
            isPushed = false
        } else
            return
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        clearArrSongOffline()

    }

}