package com.hunglee.mymusicproject.media

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.hunglee.mymusicproject.helpers.Const
import com.hunglee.mymusicproject.helpers.Const.MEDIA_IDLE
import com.hunglee.mymusicproject.helpers.Const.MEDIA_PAUSED
import com.hunglee.mymusicproject.helpers.Const.MEDIA_PLAYING
import com.hunglee.mymusicproject.helpers.Const.MEDIA_STOPPED
import com.hunglee.mymusicproject.helpers.SharePreferencesController
import com.hunglee.mymusicproject.model.Song
import kotlin.random.Random

@SuppressLint("StaticFieldLeak")
object MediaManager {

    private const val PERMISION_ALL = 265

    var mediaPlayer: MediaPlayer = MediaPlayer()
    private val mSongList = arrayListOf<Song>()

    private var currentSong = -1
    private var mediaState = MEDIA_IDLE
    private var context1: Context? = null

    fun changeMediaState(state: Int) {
        mediaState = state
    }

    fun setContext(context: Context?) {
        context1 = context
    }

    fun nextSong() {
        val sharePreference: SharePreferencesController =
            SharePreferencesController.getInstance(context1!!)
        val loop = sharePreference.getInt(Const.MEDIA_CURRENT_STATE_LOOP, Const.MEDIA_STATE_NO_LOOP)
        val shuffle = sharePreference.getBoolean(Const.MEDIA_SHUFFLE, Const.MEDIA_SHUFFLE_TRUE)
        if (loop != Const.MEDIA_STATE_LOOP_ONE) {
            if (shuffle) {
                currentSong = Random.nextInt(mSongList.size)
            } else {
                if (currentSong > mSongList.size - 2) {
                    currentSong = 0
                } else {
                    currentSong++
                }
            }
        }
        playPauseSong(true)
    }


    fun playPauseSong(isNews: Boolean = false) {
        if (mediaState == MEDIA_IDLE || mediaState == MEDIA_STOPPED || isNews) {
            //chay tu dau
            mediaPlayer.reset()
            val song = mSongList[currentSong]
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaState = MEDIA_PLAYING


        } else if (mediaState == MEDIA_PLAYING) {
            mediaPlayer.pause()
            mediaState = MEDIA_PAUSED
        } else if (mediaState == MEDIA_PAUSED) {
            mediaPlayer.start()
            mediaState = MEDIA_PLAYING
        }
        mediaPlayer.setOnCompletionListener { nextSong() }
    }

    fun preSong() {
        val sharePreference = SharePreferencesController.getInstance(context1!!)
        val loop = sharePreference.getInt(Const.MEDIA_CURRENT_STATE_LOOP, Const.MEDIA_STATE_NO_LOOP)
        val shuffle = sharePreference.getBoolean(Const.MEDIA_SHUFFLE, Const.MEDIA_SHUFFLE_TRUE)
        if (loop != Const.MEDIA_STATE_LOOP_ONE) {
            if (shuffle) {
                currentSong = Random.nextInt(mSongList.size)
            } else {
                if (currentSong <= 0) {
                    currentSong = mSongList.size - 1
                } else {
                    currentSong--
                }
            }
        }
        playPauseSong(true)
    }

    fun mediaStop() {
        mediaPlayer.stop()
        context1 = null
    }

    fun setCurrentSong(index: Int) {
        currentSong = index

    }

    fun getAllSongFromStorage(context: Context): List<Song> {
        Log.d("doanpt", "getAllSongFromStorage")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(
                (context as Activity),
                PERMISSION,
                PERMISION_ALL
            )
        } else {
            val columnsName =
                arrayOf(
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ARTIST,
                )


            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                columnsName,
                null,
                null,
                null,
                null
            )!!
            val indexData = cursor.getColumnIndex(columnsName[0])
            val indexTitle = cursor.getColumnIndex(columnsName[1])
            val indexDisplayName = cursor.getColumnIndex(columnsName[2])
            val indexDuration = cursor.getColumnIndex(columnsName[3])
            val indexAlbum = cursor.getColumnIndex(columnsName[4])
            val indexArtist = cursor.getColumnIndex(columnsName[5])

            val hasData = cursor.moveToFirst()
            if (hasData) {
                mSongList.clear()
                while (!cursor.isAfterLast) {
                    val data = cursor.getString(indexData)
                    val title = cursor.getString(indexTitle)
                    val displayName = cursor.getString(indexDisplayName)
                    val duration = cursor.getLong(indexDuration)
                    val album = cursor.getString(indexAlbum)
                    val artist = cursor.getString(indexArtist)
                    if (duplicateChecking(title, mSongList)) {
                        cursor.moveToNext()
                        continue
                    }
                    mSongList.add(
                        Song(
                            Const.DEFAULT_SONG_ID,
                            title,
                            artist,
                            null,
                            null,
                            null,
                            Const.DEFAULT_SONG_LISTEN,
                            duration.toInt(),
                            data,
                            displayName,
                            album
                        )
                    )
                    cursor.moveToNext()
                }
            }
        }
        return mSongList
    }

    private fun duplicateChecking(title1: String, mListSong: List<Song>): Boolean {
        for (item in mListSong.listIterator()) {
            if (item.title == title1)
                return true
        }
        return false
    }

    fun isChangePosition(index: Int): Boolean {
        return index != currentSong
    }

    fun getCurrentSong(): Song {
        return mSongList[currentSong]

    }

    fun getCurrentPossion(): Int = currentSong

    fun getMySongList(): ArrayList<Song> = mSongList
}