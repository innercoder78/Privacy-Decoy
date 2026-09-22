package com.privacydecoy.research.managedprobe;
import android.content.*; import android.database.Cursor; import android.net.Uri; import android.database.MatrixCursor;
public final class EarlyProvider extends ContentProvider {
 public boolean onCreate(){ Evidence.record(java.util.Objects.requireNonNull(getContext()),"provider"); return true; }
 public String getType(Uri u){return null;} public Cursor query(Uri u,String[]p,String s,String[]a,String z){return new MatrixCursor(new String[]{"none"});}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();} public int delete(Uri u,String s,String[]a){return 0;} public int update(Uri u,ContentValues v,String s,String[]a){return 0;}
}
