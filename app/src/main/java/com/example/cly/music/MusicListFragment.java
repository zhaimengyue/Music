package com.example.cly.music;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cly.music.DB.Music;
import com.example.cly.music.DB.MusicDBHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
public class MusicListFragment extends Fragment implements View.OnClickListener{

    private ImageView imageView1, imageView2, imageView3, imageViewMore;
    private boolean isSelected = false;
    private List<View> list = new ArrayList<>();
    private RecyclerView musiclist;
    private MusicAdapter adapter;
    SeekBar seekBar;
    Music CurMusic; //当前播放音乐
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CurMusic=new Music();
        CurMusic.setId(0);
        CurMusic.setTitle("null");
        View view=inflater.inflate( R.layout.fragment_music_list,container,false );//得到当前场景
        musiclist = (RecyclerView)view.findViewById( R.id.lists );

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//上下滑动
        musiclist.setLayoutManager(layoutManager);

        adapter=new MusicAdapter(getMusic());
        musiclist.setAdapter(adapter);

        setHasOptionsMenu(true);
        registerForContextMenu( musiclist );//注册上下文菜单

        imageView1 = (ImageView) view.findViewById(R.id.img1);
        imageView2 = (ImageView) view.findViewById(R.id.img2);
        imageView3 = (ImageView) view.findViewById(R.id.img3);
        imageViewMore = (ImageView) view.findViewById(R.id.img_more);
        imageViewMore.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        list.add(imageView1);
        list.add(imageView2);
        list.add(imageView3);


        //seekBar = (SeekBar) view.findViewById(R.id.playSeekBar);
        //seekBar.setOnSeekBarChangeListener(new MusicServer.MySeekBar());

        return view;
    }//初始化
    public void setColor(int color){
        TextView textView=(TextView)musiclist.findViewWithTag(adapter.getItem());
        if(textView!=null){
            textView.setTextColor(color);
        }
    }

    class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{
        private List<Music> mMusicList;
        private int mPosition = -1;
        public int getPosition(){
            return mPosition;
        }
        public MusicAdapter(List<Music> mMusicList){
            this.mMusicList=mMusicList;
        }
        public void removeItem(int position){
            mMusicList.remove( position );
            notifyDataSetChanged();//更新适配器
        }
        public Music getItem(){
            return mMusicList.get(mPosition);
        }
        public Music getotherItem(int position){
            return mMusicList.get( position );
        }
        public Music preItem(){
            mPosition-=1;
            mPosition+=getItemCount();
            mPosition=(mPosition%getItemCount());
            return mMusicList.get( mPosition );
        }
        public Music nextItem(){
            mPosition+=1;
            mPosition=(mPosition%getItemCount());
            return mMusicList.get( mPosition );
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView musicNameText;
            public ViewHolder(View view){
                super(view);
                musicNameText=(TextView)view.findViewById( R.id.musicName );
            }
        }
        public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){//control behind which is song
            View view=LayoutInflater.from( parent.getContext() ).inflate( R.layout.simple_list_item_1 ,parent,false);
            final ViewHolder holder=new ViewHolder( view );
            view.setOnClickListener( new View.OnClickListener(){
                public void onClick(View v){
                    adapter.mPosition=holder.getAdapterPosition();
                    Music music=adapter.getItem();
                    if((CurMusic.getId())==(music.getId())){
                        startMusicService("开始",music.getPath(),false);
                    }else{
                        CurMusic=music;
                        startMusicService("开始",music.getPath(),true);
                    }
                }
            } );
            return holder;
        }
        public void onBindViewHolder(final MusicAdapter.ViewHolder holder,final int position){//xianshi song name
            Music music=mMusicList.get(position);
            holder.musicNameText.setText( music.getTitle() );//整个列表
            //holder.musicNameText.setTextColor(Color.RED);//整个列表变为红色
            holder.itemView.setOnLongClickListener( new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    mPosition=holder.getAdapterPosition();
                    return false;
                }
            } );
        }
        public int getItemCount(){
            return mMusicList.size();
        }
    }//适配器
    private List<Music> getMusic() {
        List <Music> musicList = new ArrayList <>();
        Cursor cursor = getContext().getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER );
        if (cursor.moveToFirst()){
            do {
                Music music = new Music();
                music.setId( cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID ) ) );
                music.setTitle( cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ) ) );
                music.setArtist( cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) ) );
                music.setDuration( cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ) ) );
                music.setSize( cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.SIZE ) ) );
                music.setPath( cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DATA ) ) );
                musicList.add( music );
            }while(cursor.moveToNext());
        }
        return musicList;
    }//ge SD ka song
    private void   startMusicService(String option,String path,boolean ischange){
        Intent intentService=new Intent(getContext(),MusicServer.class);
        intentService.putExtra("option",option);
        intentService.putExtra( "path",path );
        intentService.putExtra("ischange",ischange);
        getActivity().startService(intentService);
    }//control behind servise
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.menu_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }//samll message
    public boolean onContextItemSelected(MenuItem item){
        Music music;
        switch(item.getItemId()){
            //case 1:
            case R.id.action_delete:
                DeleteDialog(adapter.getPosition());
                break;
            case R.id.action_check:
                music=adapter.getotherItem(adapter.getPosition());
                getinfo(music);
                break;
        }
        return true;
    }//small message xianshi and panduan
    private void DeleteDialog(final int position){//delete song
        new AlertDialog.Builder(getContext())
                .setTitle("删除歌曲")
                .setMessage("是否真的删除歌曲？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {            //既可以使用Sql语句删除，也可以使用使用delete方法删除
                                adapter.removeItem(position);
                            }
                        })
                .setNegativeButton( "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                } )
                .create()
                .show();

    }
    private void getinfo(Music music) {//xianshi song xiangxi context
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.info, null);
        String title =music.getTitle();
        String artist=music.getArtist();
        long duration=music.getDuration();
        long size=music.getSize();
        String path=music.getPath();
        ((EditText)tableLayout.findViewById(R.id.title)).setText(title);
        ((EditText)tableLayout.findViewById(R.id.artist)).setText(artist);
        ((EditText)tableLayout.findViewById(R.id.duration)).setText(duration+"");
        ((EditText)tableLayout.findViewById(R.id.size)).setText(size+"");
        ((EditText)tableLayout.findViewById(R.id.path)).setText(path);
        new AlertDialog.Builder(getContext())
                .setTitle("歌曲信息")//标题
                .setView(tableLayout)
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }

    private void endAnimator() {
        isSelected = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewMore, "rotation", 0F, 360F).setDuration(300);
        animator.setInterpolator(new BounceInterpolator());//设置插值器
        animator.start();//开始动画
        for (int i = 0; i < 3; i++) {
            ObjectAnimator.ofFloat(list.get(i), "translationY", -200 * (i + 1), 0F).setDuration(1000).start();
        }
    }

    private void startAnimator() {
        isSelected = true;
        for (int i = 0; i < 3; i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(list.get(i), "translationY", 0F, -200 * (i + 1)).setDuration(1000);
            animator.setInterpolator(new BounceInterpolator());//设置插值器
            animator.start();
        }
        ObjectAnimator.ofFloat(imageViewMore, "rotation", 0F, 360F).setDuration(300).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_more:
                if (!isSelected) {
                    startAnimator();
                } else {
                    endAnimator();
                }
                break;
            case R.id.img1:
                if(CurMusic.getTitle().equals("null")){
                    CurMusic=adapter.getotherItem(0);
                    adapter.mPosition=0;
                    //setColor(Color.BLUE);
                    startMusicService("开始",CurMusic.getPath(),true);
                }else{
                    //setColor(Color.BLUE);
                    CurMusic=adapter.preItem();
                    startMusicService("开始",CurMusic.getPath(),true);
                }
                break;
            case R.id.img2:{
                if(CurMusic.getTitle().equals("null")){
                    CurMusic=adapter.getotherItem(0);
                    adapter.mPosition=0;
                    setColor(Color.BLUE);
                    startMusicService("开始",CurMusic.getPath(),true);
                }else{
                    setColor(Color.BLUE);
                    startMusicService("开始",CurMusic.getPath(),false);
                }
            }
                break;
            case R.id.img3:{
                if(CurMusic.getTitle().equals("null")){
                    CurMusic=adapter.getotherItem(0);
                    adapter.mPosition=0;
                    //setColor(Color.BLUE);
                    startMusicService("开始",CurMusic.getPath(),true);
                }else{
                    //setColor(Color.BLUE);
                    CurMusic=adapter.nextItem();
                    startMusicService("开始",CurMusic.getPath(),true);
                }
            }
                break;
        }
    }
}
