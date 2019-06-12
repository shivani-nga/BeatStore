package com.makehitmusic.hiphopbeats.adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.BeatsObject;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;

public class BeatsAdapter extends ArrayAdapter<BeatsObject> {

    /** Tag for log messages */
    private static final String LOG_TAG = BeatsAdapter.class.getName();

    private Context context;
    private ArrayList<BeatsObject> allBeats;
    private int flag;
    private long downloadID;

    TextView optionButton;

    public BeatsAdapter(Context context, ArrayList<BeatsObject> allBeats, int flag) {
        super(context, 0, allBeats);
        this.context = context;
        this.allBeats = allBeats;
        this.flag = flag;
    }

    /**
     * Returns a list item view that displays information about the beat at the given position
     * in the list of beats.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            if (flag == 0) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.beats_list_item, parent, false);
            } else if (flag == 1) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.beats_list_item_library, parent, false);
            }
        }

        // Find the beat at the given position in the list of beats
        BeatsObject currentBeat = getItem(position);

        // Find the TextView with view ID beat_name
        TextView beatName = (TextView) listItemView.findViewById(R.id.beat_name);
        // Display the beat_name of the current beat in that TextView
        beatName.setText(currentBeat.getItemName());

        // Find the TextView with view ID producer_name
        TextView producerName = (TextView) listItemView.findViewById(R.id.producer_name);
        // Display the producer_name of the current beat in that TextView
        producerName.setText(currentBeat.getProducerName());

        // Find the TextView with view ID duration
        TextView duration = (TextView) listItemView.findViewById(R.id.duration);
        // Display the duration of the current beat in that TextView
        duration.setText(currentBeat.getItemDuration());

        // Find the ImageView with view ID beat_cover
        ImageView beatCover = (ImageView) listItemView.findViewById(R.id.beat_cover);
        // Display the beat_cover of the current beat in that ImageView
        if (!(currentBeat.getItemImageBig().equals(BASE_URL))) {
            Glide.with(context).load(currentBeat.getItemImageBig())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(beatCover);
        }
        else if (!(currentBeat.getItemImageSmall().equals(BASE_URL))) {
            Glide.with(context).load(currentBeat.getItemImageSmall())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(beatCover);
        }
        else if (!(currentBeat.getProducerImage().equals(BASE_URL))) {
            Glide.with(context).load(currentBeat.getProducerImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(beatCover);
        }
        else {
            beatCover.setImageDrawable(context.getResources().getDrawable(R.drawable.default_cover));
        }

        if (flag == 0) {

            // Find the TextView with view ID genre_name
            TextView genreName = (TextView) listItemView.findViewById(R.id.genre_name);
            // Display the genre_name of the current beat in that TextView
            genreName.setText(currentBeat.getItemDescription());

            // Find the TextView with view ID beat_price
            TextView beatPrice = (TextView) listItemView.findViewById(R.id.beat_price);
            // Display the beat_price of the current beat in that TextView
            beatPrice.setText("$" + String.valueOf(currentBeat.getItemPrice()));

            beatPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    LinearLayout rootView = (LinearLayout) parentRow.getParent();
                    ListView listView = (ListView) rootView.getParent();
                    final int position = listView.getPositionForView(rootView);
                    Log.d("Position", String.valueOf(position));

                    // Find the beat at the given position in the list of beats
                    final BeatsObject currentBeat = getItem(position);

                    (listView).performItemClick(v, position, 0);
                }
            });

            // Find the ImageView with view ID is_liked
            ImageView isLiked = (ImageView) listItemView.findViewById(R.id.is_liked);
            // Display the is_liked of the current beat in that ImageView
            if ("true".equals(currentBeat.getIsLiked())) {
                isLiked.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_24));
            } else if ("false".equals(currentBeat.getIsLiked())) {
                isLiked.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_border_24));
            }

            isLiked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    LinearLayout rootView = (LinearLayout) parentRow.getParent();
                    ListView listView = (ListView) rootView.getParent();
                    final int position = listView.getPositionForView(rootView);
                    Log.d("Position", String.valueOf(position));

                    // Find the beat at the given position in the list of beats
                    final BeatsObject currentBeat = getItem(position);

                    (listView).performItemClick(v, position, 0);
                }
            });
        }

        if (flag == 1) {
            optionButton = (TextView) listItemView.findViewById(R.id.options);

            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    LinearLayout rootView = (LinearLayout) parentRow.getParent();
                    ListView listView = (ListView) rootView.getParent();
                    final int position = listView.getPositionForView(rootView);
                    Log.d("Position", String.valueOf(position));

                    // Find the beat at the given position in the list of beats
                    final BeatsObject currentBeat = getItem(position);
                    final String samplePath = currentBeat.getItemSamplePath();

                    (listView).performItemClick(v, position, 0);

//                    //creating a popup menu
//                    PopupMenu popup = new PopupMenu(context, v);
//                    //inflating menu from xml resource
//                    MenuInflater inflater = popup.getMenuInflater();
//                    inflater.inflate(R.menu.library_menu, popup.getMenu());
//                    //displaying the popup
//                    popup.show();
//                    //adding click listener
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.email_option:
//                                    //handle email click
//
//                                    try {
//                                        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC), "MHMBeats");
//
//
//                                        if (!imageStorageDir.exists()) {
//
//                                            imageStorageDir.mkdirs();
//                                        }
//
//
//                                        String imgExtension = ".mp3";
//
//                                        String file = currentBeat.getItemName() + " - " + currentBeat.getProducerName() + imgExtension;
//
//                                        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//                                        Uri downloadUri = Uri.parse(samplePath);
//                                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//
//                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                                                .setDestinationInExternalPublicDir(DIRECTORY_MUSIC + File.separator, file)
//                                                .setTitle(file).setDescription("Saved from BeatStore")
//                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//                                        downloadID = dm.enqueue(request);
//
//                                        Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show();
//
//
//                                    } catch (IllegalStateException ex) {
//                                        Toast.makeText(getApplicationContext(),"Storage Error", Toast.LENGTH_SHORT).show();
//                                        ex.printStackTrace();
//                                    } catch (Exception ex) {
//                                        // just in case, it should never be called anyway
//                                        Toast.makeText(getApplicationContext(),"Unable to save image", Toast.LENGTH_SHORT).show();
//                                        ex.printStackTrace();
//                                    }
//
//                                    // execute this when the downloader must be fired
////                                    final DownloadTask downloadTask = new DownloadTask(context);
////                                    downloadTask.execute(samplePath, currentBeat.getItemName(), currentBeat.getProducerName());
//
////                                    Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
////                                    feedbackIntent.setType("message/rfc822");
////                                    //feedbackIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"xxx@xxx.xxx"});
////                                    feedbackIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
////                                            currentBeat.getItemName() + " from Beat Store");
////                                    context.startActivity(Intent.createChooser(feedbackIntent, "Send Beat"));
//                                    break;
//                                case R.id.drive_option:
//                                    //handle drive click
//                                    break;
//                            }
//                            return false;
//                        }
//                    });
                }
            });
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
