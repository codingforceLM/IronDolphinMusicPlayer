package de.codingforcelm.idmp.activity;

/**
 * This class provides constant variables to identify context menu items
 */
public final class MenuIdentifier {

    /**
     * Group ids for context menu items
     */
    public static final int GROUP_PLAYLISTLIST = 0;
    public static final int GROUP_SONGLIST = 1;
    public static final int GROUP_ALBUMLIST = 2;
    public static final int GROUP_ALBUM = 3;
    public static final int GROUP_PLAYLIST = 4;

    /**
     * Ids for context menu actions
     */
    public static final int ADD_TO_PLAYLIST = 101;
    public static final int ADD_TO_QUEUE = 102;
    public static final int DELETE_PLAYLIST = 103;
    public static final int REMOVE_FROM_PLAYLIST = 104;

    /**
     * This offset is used to create context submenu items in an unused area
     */
    public static final int OFFSET_PLAYLISTID = 100000;

}