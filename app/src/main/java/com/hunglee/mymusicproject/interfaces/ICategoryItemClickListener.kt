package com.hunglee.mymusicproject.interfaces

import com.hunglee.mymusicproject.model.Song

interface ICategoryItemClickListener {
//        fun onClickCategoryItem(view: View, isLongPress: Boolean)
        fun playSong(araList: List<Song>, position: Int)

}