package com.joeshuff.dddungeongenerator.screens;

import android.content.*;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.RecyclerViewEmptySupport;
import com.joeshuff.dddungeongenerator.ResultsActivity;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Room;
import com.joeshuff.dddungeongenerator.generator.floors.Floor;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.util.List;

public class ResultsFragment extends Fragment {

    //1554136508219

    private static String ARG_PAGE = "PAGE_ID";

    private static Dungeon dungeon;

    private static Context applicationContext;

    int pageId;

    View root;

    public static ResultsFragment getInstance(Context c, int page, Dungeon generatedDungeon) {
        dungeon = generatedDungeon;
        applicationContext = c;

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.pager_information_page, container, false);

        if (pageId == 1) {
            root = inflater.inflate(R.layout.pager_information_page, container, false);
            loadInfoPage();
        } else if (pageId == 2) {
            LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, new IntentFilter("goto-room"));
            root = inflater.inflate(R.layout.pager_map_page, container, false);
            loadMapPage();
        }
        
        return root;
    }

    private void loadInfoPage() {
        root.findViewById(R.id.seedView).setOnClickListener(e -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Seed", dungeon.getSeed());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(), "Copied Seed", Toast.LENGTH_LONG).show();
        });

        TextView seedTitle = root.findViewById(R.id.seedView).findViewById(R.id.infoPair_key);
        TextView seedValue = root.findViewById(R.id.seedView).findViewById(R.id.infoPair_value);

        TextView environmentTitle = root.findViewById(R.id.environmentView).findViewById(R.id.infoPair_key);
        TextView environmentValue = root.findViewById(R.id.environmentView).findViewById(R.id.infoPair_value);

        TextView creatorTitle = root.findViewById(R.id.creatorView).findViewById(R.id.infoPair_key);
        TextView creatorValue = root.findViewById(R.id.creatorView).findViewById(R.id.infoPair_value);

        TextView purposeTitle = root.findViewById(R.id.purposeView).findViewById(R.id.infoPair_key);
        TextView purposeValue = root.findViewById(R.id.purposeView).findViewById(R.id.infoPair_value);

        TextView historyTitle = root.findViewById(R.id.historyView).findViewById(R.id.infoPair_key);
        TextView historyValue = root.findViewById(R.id.historyView).findViewById(R.id.infoPair_value);

        TextView floorTitle = root.findViewById(R.id.floorCountView).findViewById(R.id.infoPair_key);
        TextView floorValue = root.findViewById(R.id.floorCountView).findViewById(R.id.infoPair_value);

        if (dungeon == null) {
            return;
        }

        seedTitle.setText("Seed");
        seedValue.setText(dungeon.getSeed());

        environmentTitle.setText("Environment");
        environmentValue.setText(dungeon.getSelectedEnvironment().getDescription());

        creatorTitle.setText("Creator");
        creatorValue.setText(dungeon.getDungeonCreator().getDescription());

        if (dungeon.getDungeonPurpose() != null) {
            purposeTitle.setText("Purpose - " + dungeon.getDungeonPurpose().getTitle());
            purposeValue.setText(dungeon.getDungeonPurpose().getDescription());
        } else {
            purposeTitle.setText("Purpose");
            purposeValue.setText("N/A");
        }

        historyTitle.setText("History");
        if (dungeon.getDungeonHistory() != null) {
            historyValue.setText(dungeon.getDungeonHistory().getDesc());
        } else {
            historyValue.setText("N/A");
        }

        floorTitle.setText("Floors");
        floorValue.setText("" + dungeon.getDungeonFloors().size());
    }

    private int selectedRoomId = -1;
    private int selectedLevel = 0;

    TickSeekBar seeker;

    private void loadMapPage() {
        fillMap(selectedLevel);

        seeker = root.findViewById(R.id.floorSelection);
        seeker.setMin(dungeon.getLowestDungeonFloor().getLevel());
        seeker.setMax(dungeon.getHighestDungeonFloor().getLevel());
        seeker.setTickCount(dungeon.getDungeonFloors().size());

        if (dungeon.getDungeonFloors().size() == 1) {
            seeker.setVisibility(View.GONE);
            ((TextView) root.findViewById(R.id.selectionTitle)).setText("1 Floor");
        }

        seeker.setProgress(selectedLevel);

        seeker.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
                selectedLevel = seekBar.getProgress();
                seekBar.setProgress(selectedLevel);
                selectedRoomId = -1;
                fillMap(selectedLevel);
            }
        });
    }

    public static Bitmap loadBitmapFromView(View v, int square) {
        Bitmap b = Bitmap.createBitmap(square,  square, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.WHITE);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    private void fillMap(int level) {
        RelativeLayout mapLayout = root.findViewById(R.id.mapLayout);
        mapLayout.removeAllViews();

        root.findViewById(R.id.clickOnARoom).setVisibility(View.GONE);
        root.findViewById(R.id.room_description_title).setVisibility(View.VISIBLE);

        if (selectedRoomId == -1) {
            root.findViewById(R.id.clickOnARoom).setVisibility(View.VISIBLE);
            root.findViewById(R.id.room_description_title).setVisibility(View.GONE);
        }

        root.findViewById(R.id.zoomLayout).getLayoutParams().height = ResultsActivity.getScreenHeight() / 2;

        int mapSquare = ResultsActivity.dpToExact(getActivity(), 800);
        Bitmap corridorMap = Bitmap.createBitmap(mapSquare, mapSquare, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(corridorMap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        for (List<Point> corr : dungeon.getDungeonFloorAtLevel(level).getAllCorridors()) {
            for (Point p : corr) {
                int oneDp = ResultsActivity.dpToExact(getActivity(), 3);

                int topLeft = ResultsActivity.dpToExact(getActivity(), p.x - 1);
                int topRight = ResultsActivity.dpToExact(getActivity(), p.y - 1);

                canvas.drawRect(new Rect(
                        topLeft,
                        topRight,
                        topLeft + oneDp,
                        topRight + oneDp), paint);
            }
        }

        ImageView imageView = new ImageView(getActivity());
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(mapSquare, mapSquare));
        imageView.setImageBitmap(corridorMap);

        mapLayout.addView(imageView);

        Floor viewingFloor = dungeon.getDungeonFloorAtLevel(level);
        if (viewingFloor == null) {
            Toast.makeText(getContext(), "Error Loading floor " + level, Toast.LENGTH_LONG).show();
            return;
        }

        for (Room r : viewingFloor.getAllRooms()) {
            View room = new View(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ResultsActivity.dpToExact(getActivity(), r.getWidth()), ResultsActivity.dpToExact(getActivity(), r.getHeight()));
            params.setMargins(ResultsActivity.dpToExact(getActivity(), r.getGlobalStartX()), ResultsActivity.dpToExact(getActivity(), r.getGlobalStartY()), 0, 0);

            room.setLayoutParams(params);

            if (r.getId() == selectedRoomId) {
                room.setBackgroundResource(R.drawable.selected_room_bg);
            } else {
                room.setBackgroundResource(R.drawable.room_bg);
            }

            room.setOnClickListener(e -> {
                roomSelected(r);
            });

            mapLayout.addView(room);
        }
    }

    private void gotoRoom(int floor, int roomId) {
        seeker.setProgress(floor);
        selectedRoomId = roomId;
        selectedLevel = floor;

        Room room = dungeon.getDungeonFloorAtLevel(selectedLevel).getAllRooms().get(0);
        try {
            room = dungeon.getDungeonFloorAtLevel(selectedLevel).getAllRooms().stream().filter(room1 -> room1.getId() == selectedRoomId).findFirst().get();
        } catch (Exception e) {}

        roomSelected(room);
        ((NestedScrollView) root.findViewById(R.id.scrollMapPage)).fullScroll(View.FOCUS_UP);
    }

    private void roomSelected(Room r) {
        selectedRoomId = r.getId();

        ((TextView) root.findViewById(R.id.roomTitle)).setText("Room " + r.getId());
        ((TextView) root.findViewById(R.id.roomDesc)).setText(r.getDetail());

        fillMap(selectedLevel);

        RecyclerViewEmptySupport recyclerView = root.findViewById(R.id.featureList);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setEmptyView(root.findViewById(R.id.emptyFeatureView));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new FeatureListAdapter(applicationContext, r.getFeatureList()));

//        ((NestedScrollView) root.findViewById(R.id.scrollMapPage)).fullScroll(View.FOCUS_DOWN);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int floor = intent.getIntExtra("floor", 0);
            int room = intent.getIntExtra("room", 0);

            gotoRoom(floor, room);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pageId == 2) {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver);
            applicationContext = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageId = getArguments().getInt(ARG_PAGE);
    }

    public static class ResultFragmentPagerAdapter extends FragmentStatePagerAdapter {

        Dungeon dungeon;
        Context c;

        public ResultFragmentPagerAdapter(AppCompatActivity a, Dungeon dungeon) {
            super(a.getSupportFragmentManager());
            c = a.getApplicationContext();
            this.dungeon = dungeon;
        }

        @Override
        public Fragment getItem(int i) {
            return ResultsFragment.getInstance(c,i + 1, dungeon);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Information";
            if (position == 1) return "Map";

            return "None";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
