package com.joeshuff.dddungeongenerator.screens.viewdungeon

import android.content.*
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.joeshuff.dddungeongenerator.R
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
import kotlinx.android.synthetic.main.pager_map_page.view.*

class ResultsFragment: Fragment() {

    companion object {
        const val PAGE_ARG = "page_id"

        var context: Context? = null
        var loadedDungeon: Dungeon? = null

        fun getInstance(context: Context, pageId: Int, dungeon: Dungeon): ResultsFragment {
            val frag = ResultsFragment()

            this.context = context
            loadedDungeon = dungeon

            val bundle = Bundle()
            bundle.putInt(PAGE_ARG, pageId)
            frag.arguments = bundle

            return frag
        }

        fun loadBitmapFromView(v: View, square: Int): Bitmap {
            val b = Bitmap.createBitmap(square, square, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            c.drawColor(Color.WHITE)
            v.layout(v.left, v.top, v.right, v.bottom)
            v.draw(c)
            return b
        }
    }

    var pageId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageId = arguments?.getInt(PAGE_ARG, 0)?: 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (arguments?.getInt(PAGE_ARG, 0) == 2) LocalBroadcastManager.getInstance(context).registerReceiver(receiver, IntentFilter("goto-room"))
    }

    var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.pager_information_page, container, false)

        if (pageId == 1) {
            view = inflater.inflate(R.layout.pager_information_page, container, false)
            loadInfoPage(view)
        } else if (pageId == 2) {
            view = inflater.inflate(R.layout.pager_map_page, container, false)
            loadMapPage(view)
        }

        root = view

        return view
    }

    private fun loadInfoPage(rootView: View) {
        context?.let { context ->
            loadedDungeon?.let { dungeon ->
                rootView.seedView.setOnClickListener { e: View? ->
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
        }
    }

    private var selectedRoomId = -1
    private var selectedLevel = 0
    var seeker: TickSeekBar? = null
    private fun loadMapPage(rootView: View) {
        context?.let { context ->
            loadedDungeon?.let { dungeon ->
                fillMap(context, rootView, selectedLevel, dungeon)

                rootView.floorSelection.min = dungeon.lowestDungeonFloor?.level?.toFloat()?: 0f
                rootView.floorSelection.max = dungeon.highestDungeonFloor?.level?.toFloat()?: 0f
                rootView.floorSelection.tickCount = dungeon.getDungeonFloors().size

                if (dungeon.getDungeonFloors().size == 1) {
                    rootView.floorSelection.makeGone()
                    rootView.selectionTitle.text = "1 Floor"
                }

                rootView.floorSelection.setProgress(selectedLevel.toFloat())

                rootView.floorSelection.onSeekChangeListener = object : OnSeekChangeListener {
                    override fun onSeeking(seekParams: SeekParams) {}
                    override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
                    override fun onStopTrackingTouch(seekBar: TickSeekBar) {
                        selectedLevel = seekBar.progress
                        seekBar.setProgress(selectedLevel.toFloat())
                        selectedRoomId = -1
                        fillMap(context, rootView, selectedLevel, dungeon)
                    }
                }
            }
        }

    }

    private fun fillMap(context: Context, rootView: View, level: Int, dungeon: Dungeon) {
        with (rootView) {
            mapLayout.removeAllViews()
            clickOnARoom.makeGone()

            room_description_title.makeVisible()

            if (selectedRoomId == -1) {
                clickOnARoom.makeVisible()
                room_description_title.makeGone()
            }

            zoomLayout.layoutParams.height = screenHeight() / 2

            val mapSquare: Int = context.dpToExact(800f)
            val corridorMap = Bitmap.createBitmap(mapSquare, mapSquare, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(corridorMap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = ContextCompat.getColor(context, R.color.colorCorridor)

            dungeon.getDungeonFloorAtLevel(level)?.allCorridors?.forEach {
                for (section in it.sections) {
                    val topLeft: Int = parentContext.dpToExact((section.startX).toFloat())
                    val topRight: Int = parentContext.dpToExact((section.startY).toFloat())

                    canvas.drawRect(Rect(
                            topLeft,
                            topRight,
                            topLeft + parentContext.dpToExact(section.width.toFloat()),
                            topRight + parentContext.dpToExact(section.height.toFloat())), paint)
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
                    val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(context.dpToExact(r.width), context.dpToExact(r.height))
                    params.setMargins(context.dpToExact(r.globalStartX), context.dpToExact(r.globalStartY), 0, 0)
                    room.layoutParams = params

                    if (r.id == selectedRoomId) {
                        room.setBackgroundResource(R.drawable.selected_room_bg)
                    } else {
                        room.setBackgroundResource(R.drawable.room_bg)
                    }

                    room.setOnClickListener { e: View? -> roomSelected(r, context, dungeon) }
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

        context?.let { context ->
            loadedDungeon?.let { dungeon ->
                var room = dungeon.getDungeonFloorAtLevel(selectedLevel)?.allRooms?.firstOrNull { it.id == selectedRoomId }

                room?.let {
                    roomSelected(it, context, dungeon)
                    root?.scrollMapPage?.fullScroll(View.FOCUS_UP)
                }
            }
        }
    }

    private fun roomSelected(r: Room, context: Context, dungeon: Dungeon) {
        selectedRoomId = r.id

        root?.let {
            it.roomTitle.text = "Room ${r.id}"
            it.roomDesc.text = r.detail

            fillMap(context, it, selectedLevel, dungeon)

            it.featureList.makeVisible()
            it.featureList.setLayoutManager(LinearLayoutManager(context))
            it.featureList.setAdapter(FeatureListAdapter(parentContext, r.getUIFeatures()))
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
            context?.let {
                LocalBroadcastManager.getInstance(it).unregisterReceiver(receiver)
            }
        }
    }

    class ResultFragmentPagerAdapter(val a: Context, fm: FragmentManager, val dungeon: Dungeon) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(i: Int): Fragment {
            return getInstance(a, i + 1, dungeon)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0) return "Information"
            return if (position == 1) "Map" else "None"
        }

        override fun getCount(): Int {
            return 2
        }
    }
}