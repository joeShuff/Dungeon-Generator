package com.joeshuff.dddungeongenerator.screens.viewdungeon

import android.content.*
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.RecyclerViewEmptySupport
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.util.dpToExact
import com.joeshuff.dddungeongenerator.util.makeGone
import com.joeshuff.dddungeongenerator.util.makeVisible
import com.joeshuff.dddungeongenerator.util.screenHeight
import com.warkiz.tickseekbar.OnSeekChangeListener
import com.warkiz.tickseekbar.SeekParams
import com.warkiz.tickseekbar.TickSeekBar
import kotlinx.android.synthetic.main.element_info_pair.view.*
import kotlinx.android.synthetic.main.pager_information_page.view.*
import kotlinx.android.synthetic.main.pager_map_page.*
import kotlinx.android.synthetic.main.pager_map_page.clickOnARoom
import kotlinx.android.synthetic.main.pager_map_page.mapLayout
import kotlinx.android.synthetic.main.pager_map_page.room_description_title
import kotlinx.android.synthetic.main.pager_map_page.view.*

class ResultsFragment(val parentContext: Context, val pageId: Int, val dungeon: Dungeon): Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (pageId == 2) LocalBroadcastManager.getInstance(context).registerReceiver(receiver, IntentFilter("goto-room"))
    }

    var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pager_information_page, container, false)

        if (pageId == 1) {
            val infoPage = inflater.inflate(R.layout.pager_information_page, container, false)
            loadInfoPage(infoPage)
        } else if (pageId == 2) {
            val mapPage = inflater.inflate(R.layout.pager_map_page, container, false)
            loadMapPage(mapPage)
        }

        root = view

        return view
    }

    private fun loadInfoPage(rootView: View) {
        rootView.seedView.setOnClickListener { e: View? ->
            val clipboard = parentContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Seed", dungeon.getSeed())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied Seed", Toast.LENGTH_LONG).show()
        }

        with (rootView) {
            seedView.infoPair_key.text = "Seed"
            seedView.infoPair_value.text = dungeon.getSeed()
            environmentView.infoPair_key.text = "Environment"
            environmentView.infoPair_value.text = dungeon.selectedEnvironment?.description?: "None"
            creatorView.infoPair_key.text = "Creator"
            creatorView.infoPair_value.text = dungeon.dungeonCreator?.getDescription()?: "None"

            purposeView.infoPair_key.text = "Purpose" + (if (dungeon.dungeonPurpose != null) " - " + dungeon.dungeonPurpose?.title else "")
            purposeView.infoPair_value.text = dungeon.dungeonPurpose?.let { it.description }?: run { "N/A" }

            historyView.infoPair_key.text = "History"
            historyView.infoPair_value.text = dungeon.dungeonHistory?.let { it.desc }?: run { "N/A" }

            floorCountView.infoPair_key.text = "Floors"
            floorCountView.infoPair_value.text = "${dungeon.getDungeonFloors().size}"
        }
    }

    private var selectedRoomId = -1
    private var selectedLevel = 0
    var seeker: TickSeekBar? = null
    private fun loadMapPage(rootView: View) {
        fillMap(rootView, selectedLevel)

        floorSelection.min = dungeon.lowestDungeonFloor?.level?.toFloat()?: 0f
        floorSelection.max = dungeon.highestDungeonFloor?.level?.toFloat()?: 0f
        floorSelection.tickCount = dungeon.getDungeonFloors().size

        if (dungeon.getDungeonFloors().size == 1) {
            floorSelection.makeGone()
            selectionTitle.text = "1 Floor"
        }

        floorSelection.setProgress(selectedLevel.toFloat())

        floorSelection.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {}
            override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar) {
                selectedLevel = seekBar.progress
                seekBar.setProgress(selectedLevel.toFloat())
                selectedRoomId = -1
                fillMap(rootView, selectedLevel)
            }
        }
    }

    private fun fillMap(rootView: View, level: Int) {
        with (rootView) {
            mapLayout.removeAllViews()
            clickOnARoom.makeGone()

            room_description_title.makeVisible()

            if (selectedRoomId == -1) {
                clickOnARoom.makeVisible()
                room_description_title.makeGone()
            }

            zoomLayout.layoutParams.height = screenHeight() / 2

            val mapSquare: Int = parentContext.dpToExact(800f)
            val corridorMap = Bitmap.createBitmap(mapSquare, mapSquare, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(corridorMap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = ContextCompat.getColor(parentContext, R.color.colorCorridor)

            dungeon.getDungeonFloorAtLevel(level)?.allCorridors?.forEach {
                for (p in it) {

                    val oneDp: Int = parentContext.dpToExact(3f)
                    val topLeft: Int = parentContext.dpToExact((p.x - 1).toFloat())
                    val topRight: Int = parentContext.dpToExact((p.y - 1).toFloat())

                    canvas.drawRect(Rect(
                            topLeft,
                            topRight,
                            topLeft + oneDp,
                            topRight + oneDp), paint)

                }
            }

            val imageView = ImageView(activity)
            imageView.layoutParams = RelativeLayout.LayoutParams(mapSquare, mapSquare)
            imageView.setImageBitmap(corridorMap)
            mapLayout.addView(imageView)

            val viewingFloor = dungeon.getDungeonFloorAtLevel(level)

            viewingFloor?.let {
                for (r in it.allRooms) {
                    val room = View(context)
                    val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(parentContext.dpToExact(r.width), parentContext.dpToExact(r.height))
                    params.setMargins(parentContext.dpToExact(r.globalStartX), parentContext.dpToExact(r.globalStartY), 0, 0)
                    room.layoutParams = params

                    if (r.id == selectedRoomId) {
                        room.setBackgroundResource(R.drawable.selected_room_bg)
                    } else {
                        room.setBackgroundResource(R.drawable.room_bg)
                    }

                    room.setOnClickListener { e: View? -> roomSelected(r) }
                    mapLayout.addView(room)
                }
            }?: run {
                Toast.makeText(context, "Error Loading floor $level", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    private fun gotoRoom(floor: Int, roomId: Int) {
        root?.floorSelection?.setProgress(floor.toFloat())
        selectedRoomId = roomId
        selectedLevel = floor

        var room = dungeon.getDungeonFloorAtLevel(selectedLevel)?.allRooms?.firstOrNull { it.id == selectedRoomId }

        room?.let {
            roomSelected(it)
            root?.scrollMapPage?.fullScroll(View.FOCUS_UP)
        }
    }

    private fun roomSelected(r: Room) {
        selectedRoomId = r.id

        root?.let {
            it.roomTitle.text = "Room $r.id"
            it.roomDesc.text = r.detail

            fillMap(it, selectedLevel)

            it.featureList.makeVisible()
            it.featureList.setEmptyView(it.emptyFeatureView)
            it.featureList.layoutManager = LinearLayoutManager(context)
            it.featureList.adapter = FeatureListAdapter(context, r.getFeatureList())
        }
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val floor = intent.getIntExtra("floor", 0)
            val room = intent.getIntExtra("room", 0)
            gotoRoom(floor, room)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pageId == 2) {
            LocalBroadcastManager.getInstance(parentContext).unregisterReceiver(receiver)
        }
    }

    class ResultFragmentPagerAdapter(val a: AppCompatActivity, val dungeon: Dungeon) : PagerAdapter() {
        fun getItem(i: Int): Fragment {
            return ResultsFragment(a, i, dungeon)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0) return "Information"
            return if (position == 1) "Map" else "None"
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return getItem(position)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as ImageView
        }

        override fun getCount(): Int {
            return 2
        }
    }

    companion object {
        fun loadBitmapFromView(v: View, square: Int): Bitmap {
            val b = Bitmap.createBitmap(square, square, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            c.drawColor(Color.WHITE)
            v.layout(v.left, v.top, v.right, v.bottom)
            v.draw(c)
            return b
        }
    }
}