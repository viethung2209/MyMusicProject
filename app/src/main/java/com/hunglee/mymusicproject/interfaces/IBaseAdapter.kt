package com.hunglee.mymusicproject.interfaces

interface IBaseAdapter<D> {
    fun getItemCount(): Int
    fun getData(position: Int): D
    fun onClickItem(position: Int)
}