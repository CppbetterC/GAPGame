package cppbetterc.gapgame;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by USER on 2017/5/7.
 */

public class LoadingGameAdapter extends BaseAdapter{

    private  String [][] gameList ;
    private LayoutInflater inflater;
    private int indentionBase;

    static class ViewHolder{
        LinearLayout rlBorder;
        TextView gameName;
        TextView gameHost;
    }
    public LoadingGameAdapter(String[][] data ,LayoutInflater inflater){
        this.gameList = data;
        this.inflater = inflater;
        indentionBase = 100;
    }
    @Override
    public int getCount() {
        return gameList.length;
    }

    @Override
    public Object getItem(int position) {
        return gameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.loadgame_style_listview,null);
            holder.gameName = (TextView) convertView.findViewById(R.id.loading_game_name);
            holder.gameHost =(TextView) convertView.findViewById(R.id.loading_game_host);
            holder.rlBorder = (LinearLayout) convertView.findViewById(R.id.llBorder);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.gameName.setText(gameList[position][0]);
        holder.gameHost.setText(gameList[position][1]);
//        holder.rlBorder.setBackgroundColor(Color.parseColor("#87CEEB"));
        return convertView;
    }
}
