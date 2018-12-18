package com.example.cly.music;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MusicListFragment extends Fragment {

    private ListView MusicList;
    private List<String> dataList;
    private MusicAdapter adapter;
    private List<Music> List;
    private TextView SelectMusic;
    private Button startorstop;
    private Button predict;
    private Button next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_music_list,container,false );
        RecyclerView musiclist = (RecyclerView)view.findViewById( R.id.lists );
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        musiclist.setLayoutManager(layoutManager);
        adapter=new MusicAdapter(getMusic());
        musiclist.setAdapter(adapter);
        setHasOptionsMenu(true);
        registerForContextMenu( musiclist );//注册上下文菜单

        MusicList=view.findViewById( R.id.musiclist );
        SelectMusic=view.findViewById( R.id.SelectMusic );
        startorstop=view.findViewById( R.id.startorstop );
        predict=view.findViewById( R.id.pre );
        next=view.findViewById( R.id.next );
        return view;
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
            notifyDataSetChanged();
        }
        public Music getItem(int position){
            return mMusicList.get( position );
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView musicNameText;
            public ViewHolder(View view){
                super(view);
                musicNameText=(TextView)view.findViewById( R.id.musicName );
            }
        }
        public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            View view=LayoutInflater.from( parent.getContext() ).inflate( R.layout.simple_list_item_1 ,parent,false);
            final ViewHolder holder=new ViewHolder( view );
            view.setOnClickListener( new View.OnClickListener(){
                public void onClick(View v){
                    Music music=mMusicList.get( holder.getAdapterPosition() );
                    //点击后跳转
                }
            } );
            return holder;
        }
        public void onBindViewHolder(final MusicAdapter.ViewHolder holder,final int position){
            Music music=mMusicList.get(position);
            holder.musicNameText.setText( music.getTitle() );
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
    }
    private List<Music> getMusic(){
        List<Music> musicList=new ArrayList <>(  );
        for(int i=0;i<5;i++){
            Music music =new Music();
            music.setTitle( "ttttt" );
            musicList.add( music );
        }
        return musicList;
    }
}
