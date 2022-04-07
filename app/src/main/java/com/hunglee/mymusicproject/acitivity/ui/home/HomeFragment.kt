package com.hunglee.mymusicproject.acitivity.ui.home

import android.Manifest
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hunglee.mymusicproject.R
import com.hunglee.mymusicproject.acitivity.TrackActivity
import com.hunglee.mymusicproject.adapter.MainRecycleAdapter
import com.hunglee.mymusicproject.databinding.FragmentHomeBinding
import com.hunglee.mymusicproject.helpers.Const
import com.hunglee.mymusicproject.interfaces.IBaseAdapter
import com.hunglee.mymusicproject.interfaces.ICategoryItemClickListener
import com.hunglee.mymusicproject.media.MediaManager
import com.hunglee.mymusicproject.model.AllCategory
import com.hunglee.mymusicproject.model.Song
import com.hunglee.mymusicproject.services.MusicService


class HomeFragment : Fragment(), View.OnClickListener {

    private var data: ByteArray? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var mainCategoryRecycler: RecyclerView? = null
    private lateinit var mainRecyclerAdaper: MainRecycleAdapter
    private val songCharts2: MutableList<Song> = mutableListOf()
    private val recommendedSong: MutableList<Song> = mutableListOf()
    private val allCategory: MutableList<AllCategory> = mutableListOf()
    private val intentFilter = IntentFilter()
    private val updatePlayNewSong = UpdatePlayNewSong()
    val myFragment: FragmentManager? = null
    var mf: Fragment? = null
    var isFirstRun : Boolean = true


    //    private var trackActivity: TrackActivity? = null
    private var musicService = MusicService()


    var root: View? = null
    var mSongList: List<Song> = emptyList()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        root = binding!!.root


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE

            ) != PackageManager.PERMISSION_DENIED
        ) {
            mSongList = MediaManager.getAllSongFromStorage(requireContext())
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }

//        this.trackActivity = TrackActivity()


        dataOffline()
        setMainCategoryRecycler()
        initComponent()
        setOnClick()
        if (isMyServiceRunning(MusicService::class.java))
            showBottomLayout(true)
        else
            showBottomLayout(false)


        this.runnable.run()

        intentFilter.addAction(Const.ACTION_SEND_DATA)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(updatePlayNewSong, intentFilter)


        //not working with MarqueeTextView
//        bindingRowItemBinding = CatRowItemBinding.inflate(layoutInflater)
//        offCatTv = bindingRowItemBinding.tvTitle
//        offCatTv!!.isSelected = true
//        offCatTv!!.setHorizontallyScrolling(true)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentHomeBinding.inflate(layoutInflater)
//        setMainCategoryRecycler()
        homeViewModel.getSongCharts()
        homeViewModel.getRecommendedSong()
//        setMainCategoryRecycler()
//        initComponent()

    }


    private fun initComponent() {

        binding!!.mainRecycler.adapter = mainRecyclerAdaper
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        homeViewModel.songCharts.observe(viewLifecycleOwner) {
            songCharts2.clear()
            if (it != null) {
                songCharts2.addAll(it)
            }
            allCategory.add(AllCategory("Top 100 VietNam Music", songCharts2))
            mainRecyclerAdaper.notifyDataSetChanged()
            Log.d("doanpt", "size top 100: ${songCharts2.size}")

        }
        homeViewModel.recommenedSong.observe(viewLifecycleOwner) {
            recommendedSong.clear()
            if (it != null) {
                recommendedSong.addAll(it)
            }
            allCategory.add(AllCategory("Recommended For You", recommendedSong))
            mainRecyclerAdaper.notifyDataSetChanged()
            Log.d("doanpt", "size Recommend: ${recommendedSong.size}")

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMainCategoryRecycler() {

        mainCategoryRecycler = root!!.findViewById(R.id.main_recycler)
        val layoutManager = LinearLayoutManager(context)
        mainCategoryRecycler!!.layoutManager = layoutManager
        initMainCate()
    }

    private fun initMainCate() {
        mainRecyclerAdaper = MainRecycleAdapter(requireContext(), object :
            IBaseAdapter<AllCategory> {
            override fun getItemCount(): Int = allCategory.size

            override fun getData(position: Int): AllCategory = allCategory[position]

            override fun onClickItem(position: Int) {
                TODO("Not yet implemented")
            }

        }, object : ICategoryItemClickListener {
            override fun playSong(araList: List<Song>, position: Int) {
                showBottomLayout(true)
                if (isMyServiceRunning(MusicService::class.java)) {
                    val intent = Intent(requireContext(), MusicService::class.java)
                    requireContext().startService(intent)
                }
                requireContext().bindService(
                    Intent(requireContext(), MusicService::class.java),
                    connection,
                    Context.BIND_AUTO_CREATE
                )
                if (MediaManager.isChangePosition(position)) {
                    MediaManager.setCurrentSong(position)
                    val intent = Intent()
                    intent.action = Const.ACTION_PLAY_NEW
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                    Log.d("doanpt", "Send ACTION PLAY NEW")
                    musicService.playPauseSong(true)
                    binding!!.bottomMenu.tvBottomTitleSong.text = MediaManager.getCurrentSong().title
                    binding!!.bottomMenu.tvBottomNameArtist.text =
                        MediaManager.getCurrentSong().artistsNames
                }
                showBottomLayout(true)


//                    MediaManager.getCurrentSong().artistsNames
//                trackActivity.showBottomLayout(true)
//                mainActivity.setInforBottomLayout(
//                    list.get(position).getDisplayName(),
//                    list.get(position).getArtist()
//                )
            }

        })
        mainCategoryRecycler!!.adapter = mainRecyclerAdaper

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                runMusicActivity()
                MediaManager.getAllSongFromStorage(requireContext())
            } else {
                Toast.makeText(requireContext(), "Not have Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dataOffline() {
        Log.d("doanpt", "data offline " + mSongList.size.toString())
        val categoryItemList: MutableList<Song> = ArrayList()
        for (item in mSongList) {
            categoryItemList.add(
                Song(
                    item.id,
                    item.title,
                    item.artistsNames,
                    item.thumbnail,
                    item.thumbnailMedium,
                    item.lyric,
                    item.listen,
                    item.duration,
                    item.path,
                    item.fileName,
                    item.album,
                    item.isLiked
                )
            )
        }
        allCategory.add(AllCategory("Offline Music", categoryItemList))

    }

    private fun dataOnline(): MutableList<AllCategory> {
        //here add data to out model class
        //1st category
        val categoryItemList: MutableList<Song> = ArrayList()
        for (item in songCharts2) {
            categoryItemList.add(
                Song(
                    item.id,
                    item.title,
                    item.artistsNames,
                    item.thumbnail,
                    item.thumbnailMedium,
                    item.lyric,
                    item.listen,
                    item.duration,
                    item.path,
                    item.fileName,
                    item.album,
                    item.isLiked
                )
            )
        }


        val allCategory: MutableList<AllCategory> = ArrayList()
        allCategory.add(AllCategory("Recommended for you", categoryItemList))
        allCategory.add(AllCategory("Recommened", songCharts2))



        return allCategory
    }


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MusicService.MusicBinder = service as MusicService.MusicBinder
            musicService = binder.getMusicService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(requireContext(), MusicService::class.java) as? ActivityManager
        if (manager == null) {
            return false
        } else {
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

    fun showBottomLayout(isShow: Boolean) {
        if (isShow) {
            binding!!.bottomMenu.root.visibility = View.VISIBLE
        } else {
            binding!!.bottomMenu.root.visibility = View.GONE
        }
    }

    private var runnable: Runnable = object : Runnable {
        override fun run() {
//            if (mf != null) {
//                if (binding.bottomMenu.root.isVisible && mf!!.isVisible) {
//                    updateInforBottomLayout()
//                }
//            }
//            mf = myFragment?.findFragmentById(R.id.nav_home)
//            if (mf!= null) {
//                Log.d("doanpt", "Home fragment")
//            }
//            else
//                Log.d("doanpt", "Non Home Fragment")
//            if (binding.bottomMenu.root.isVisible)
            updateInforBottomLayout()
            Handler().postDelayed(this, 200)
        }
    }

    private fun updateInforBottomLayout() {
//        if (isMyServiceRunning(MusicService::class.java))
//            showBottomLayout(false)
//        else
//            showBottomLayout(true)
        if (binding != null) {
            if (MediaManager.mediaPlayer.isPlaying) {
                binding!!.bottomMenu.imvPausePlay.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                binding!!.bottomMenu.imvPausePlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            if (MediaManager.getCurrentPossion() != -1) {

                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(MediaManager.getCurrentSong().path)
                data = mmr.embeddedPicture
                if (data != null) {
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                    binding!!.bottomMenu.imvImageSong.setImageBitmap(bitmap)
                } else {
                    binding!!.bottomMenu.imvImageSong.setImageResource(R.drawable.ic_music)
                }
                binding!!.bottomMenu.imvImageSong.adjustViewBounds = true

                binding!!.bottomMenu.tvBottomTitleSong.text = MediaManager.getCurrentSong().title
                binding!!.bottomMenu.tvBottomNameArtist.text =
                    MediaManager.getCurrentSong().artistsNames
            }
        }


    }

    private fun setOnClick() {
        binding!!.bottomMenu.imvPausePlay.setOnClickListener(this)
        binding!!.bottomMenu.imvNext.setOnClickListener(this)
        binding!!.bottomMenu.imvPrevious.setOnClickListener(this)
        binding!!.bottomMenu.llDetailTitleSong.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent()
        when (v!!.id) {

            R.id.imv_next -> {
                intent.action = Const.ACTION_NEXT
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)


            }
            R.id.imv_previous -> {
                intent.action = Const.ACTION_PREVIOUS
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
            R.id.imv_pause_play -> {
                intent.action = Const.ACTION_PAUSE_SONG
                Log.d("doanpt", "Send ACTION PAUSE PLAY from button")
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
            R.id.ll_detail_title_song -> {
                val clickIntent = Intent(requireContext(), TrackActivity::class.java)
                startActivity(clickIntent)

            }
        }
    }


    open inner class UpdatePlayNewSong : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Const.ACTION_SEND_DATA -> setInforBottomLayout(
                    intent.getStringExtra(Const.KEY_TITLE_SONG),
                    intent.getStringExtra(Const.KEY_NAME_ARTIST)
                )
            }
        }
    }

    fun setInforBottomLayout(nameSong: String?, nameArtist: String?) {
        if (binding != null) {
            binding!!.bottomMenu.tvBottomTitleSong.text = nameSong
            binding!!.bottomMenu.tvBottomNameArtist.text = nameArtist
        }
    }


    override fun onDestroy() {

        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updatePlayNewSong)
        if (isMyServiceRunning(MusicService::class.java)) {
            requireContext().unbindService(connection)
        }
    }


    override fun onResume() {
        musicService.setContextFromMS(requireContext())
        super.onResume()
        if (MediaManager.mediaPlayer.isPlaying)
            isFirstRun = false
        if (isFirstRun)
            showBottomLayout(false)
        else {
            showBottomLayout(true)

        }

    }

    override fun onDetach() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updatePlayNewSong)
        if (isMyServiceRunning(MusicService::class.java)) {
            requireContext().unbindService(connection)
        }
        super.onDetach()
    }


}