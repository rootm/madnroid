package com.example.muvindu.recyclerdemo.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

/**
 * Provides various playlist-related utility functions.
 */
public class PlayList {
    /**
     * Queries all the playlists known to the MediaStore.
     *
     * @param resolver A ContentResolver to use.
     * @return The queried cursor.
     */
    public static Cursor queryPlaylists(ContentResolver resolver) {
        Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String sort = MediaStore.Audio.Playlists.NAME;
        return resolver.query(media, projection, null, null, sort);
    }

    public static Cursor listSongs(String name, ContentResolver resolver) {


        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", getPlaylist(resolver, name));
        Cursor cursor = resolver.query(uri, null, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER + " DESC");

        //songExists(0);
        return cursor;
    }


    public static long getPlaylist(ContentResolver resolver, String name) {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
            cursor.close();
        }

        return id;
    }

    /**
     * Create a new playlist with the given name. If a playlist with the given
     * name already exists, it will be overwritten.
     *
     * @param resolver A ContentResolver to use.
     * @param name     The name of the playlist.
     * @return The id of the new playlist.
     */
    public static long createPlaylist(ContentResolver resolver, String name) {
        long id = getPlaylist(resolver, name);

        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            // We are overwriting an existing playlist. Clear existing songs.
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }

        return id;
    }

    /**
     * Run the given query and add the results to the given playlist. Should be
     * run on a background thread.
     *
     * @param resolver A ContentResolver to use.
     * @param name     The MediaStore.Audio.Playlist id of the playlist to
     *                 modify.
     * @param audioId  The query to run. The audio id should be the first column.
     * @return The number of songs that were added to the playlist.
     */
    public static void addToPlaylist(ContentResolver resolver, Context context, String name, Long audioId) {

        try {
            String[] cols = new String[]{
                    "count(*)"
            };

            // Find the greatest PLAY_ORDER in the playlist
            //Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", getPlaylist(context.getContentResolver(),name));
            //String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
            //  Cursor cursor = resolver.query(uri, projection, null, null, null);
            //    int base = 0;
            //      if (cursor.moveToLast())
            //            base = cursor.getInt(0) + 1;
            //          cursor.close();


//
            // ContentValues value = new ContentValues(2);
            //   value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1));
            //value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
            // resolver.insert(uri, value);


            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", getPlaylist(context.getContentResolver(), name));

            Cursor cur = resolver.query(uri, cols, null, null, null);
            cur.moveToFirst();
            final int base = cur.getInt(0);
            cur.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + audioId);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
            uri = resolver.insert(uri, values);

            Log.e("list", "Song added to the play list");
            Toast.makeText(context.getApplicationContext(), "Song added to the play list", Toast.LENGTH_SHORT);

            String s = "";


        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Error when adding song", Toast.LENGTH_SHORT);
            Log.e("list", "Error when adding song");

        }


    }

    /**
     * Delete the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id       The Media.Audio.Playlists id of the playlist.
     */
    public static void deletePlaylist(ContentResolver resolver, long id) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }

    /**
     * Rename the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id       The Media.Audio.Playlists id of the playlist.
     * @param newName  The new name for the playlist.
     */
    public static void renamePlaylist(ContentResolver resolver, long id, String newName) {
        long existingId = getPlaylist(resolver, newName);
        // We are already called the requested name; nothing to do.
        if (existingId == id)
            return;
        // There is already a playlist with this name. Kill it.
        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
    }
}
