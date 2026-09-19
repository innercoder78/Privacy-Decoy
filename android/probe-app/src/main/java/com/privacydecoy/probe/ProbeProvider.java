package com.privacydecoy.probe;

public final class ProbeProvider extends android.content.ContentProvider {
    @Override public boolean onCreate() { ProbeEntry.providerCreated = true; return true; }
    @Override public android.database.Cursor query(android.net.Uri u, String[] p, String s, String[] a, String o) { return null; }
    @Override public String getType(android.net.Uri u) { return null; }
    @Override public android.net.Uri insert(android.net.Uri u, android.content.ContentValues v) { return null; }
    @Override public int delete(android.net.Uri u, String s, String[] a) { return 0; }
    @Override public int update(android.net.Uri u, android.content.ContentValues v, String s, String[] a) { return 0; }
}
