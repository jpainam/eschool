package com.edis.eschool.MyAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edis.eschool.DetailNotification;
import com.edis.eschool.R;
import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.sql.Databases;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapteur extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Notifications> mData= new ArrayList<>();
    private List<Notifications> mDataSeach= new ArrayList<>();
    private List<Notifications> mDataSave= new ArrayList<>();// table qui permet la sauvegarde des donnée l
    Databases myDb;

    public NotificationsAdapteur(Context mContext) {
        this.mContext = mContext;
        myDb = new Databases(mContext);
    }

    public void add(Notifications notification){
        if(!existe(notification,mData))
           mData.add(0,notification);
        if(!existe(notification,mDataSave))
           mDataSave.add(0,notification);
        notifyDataSetChanged();
    }
    public void updatecheck(Notifications notifications,final RecyclerView.ViewHolder holder ){
        myDb.updcheckNotification(notifications.getIdnotification());
        ((TextViewHolder) holder).notiflu.setImageResource(R.drawable.ic_check_red_24dp);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater=LayoutInflater.from(mContext);
        if(viewType == 1) { //for empty layout// text envoie
            view=mInflater.inflate(R.layout.neux_notification,parent,false);
            return new TextViewHolder(view);
        } else{
            view=mInflater.inflate(R.layout.neux_notification,parent,false);
            return new TextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Notifications c=mData.get(position);
        String type=c.getType();
        if(holder instanceof TextViewHolder ){//  message de type texte
            if(mData.get(position).getImage()!=0){
                //   Picasso.with(mContext).load(mData.ge t(position).getPub()).placeholder(R.drawable.george).into(((ImageViewHolder) holder).pub);
            /*  Glide.with(mContext)
                        .load(mData.get(position).getImage())
                        .into(((TextViewHolder) holder).icone);*/
                ((TextViewHolder) holder).icone.setImageResource(R.drawable.googleg_standard_color_18);
            }


            ((TextViewHolder) holder).titre.setText(mData.get(position).getTitre());
            ((TextViewHolder) holder).message.setText(mData.get(position).getMessage());
            ((TextViewHolder) holder).date.setText(mData.get(position).getDate());
            if(mData.get(position).getLu()==1)
               ((TextViewHolder) holder).notiflu.setImageResource(R.drawable.ic_check_red_24dp);


            ((TextViewHolder) holder).itemnotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatecheck(mData.get(position),((TextViewHolder)holder));
                    Intent detailNotifi= new Intent(mContext, DetailNotification.class);
                    detailNotifi.putExtra("notifications",mData.get(position));
                    mContext.startActivity(detailNotifi);
                    ((Activity)mContext).overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);//transition simple
                  //  ((Activity)mContext).finish();
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Notifications c=mData.get(position);
        String type=c.getType();

        if(type.equals("absence")){
            return 1;
        }else{
            return 1;
        }

    }

    public void removeNotification(int position) {
        myDb.deleteNotifiction(mData.get(position).getIdnotification());
        mData.remove(position);
        notifyDataSetChanged();
    }
    public boolean existe(Notifications notification ,List<Notifications> liste){
        for(Notifications item:liste){
            if((item.getIdnotification()==notification.getIdnotification()) ){
               return true;
            }
        }
        return false ;
    }


    public void serchNotifi(String texte) {
        for(Notifications item:mDataSave){
            if((item.getDate().toLowerCase().toLowerCase().contains(texte.toLowerCase()))
                    ||(item.getTitre().toLowerCase().toLowerCase().contains(texte.toLowerCase()))
                    ||(item.getMessage().toLowerCase().toLowerCase().contains(texte.toLowerCase()))
                    ||(item.getType().toLowerCase().toLowerCase().contains(texte.toLowerCase()))){

                if(!existe(item,mDataSeach))
                   mDataSeach.add(0,item);
            }
        }
       /// mData.retainAll( mData);
       // mData=mDataSeach;
         if(mDataSeach.size()>0){
            mData= mDataSeach;
          }
        if(texte.isEmpty()){
            mData= mDataSave;
        }
        notifyDataSetChanged();
    }

    public static class TextViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView icone;
        TextView titre;
        TextView message;
        TextView date;
        LinearLayout itemnotification;
        ImageView notiflu;
        public TextViewHolder(View itemView) {
            super(itemView);
            icone=(CircleImageView)itemView.findViewById(R.id.icone);
            titre=(TextView)itemView.findViewById(R.id.titre);
            message=(TextView)itemView.findViewById(R.id.message);
            date=(TextView)itemView.findViewById(R.id.date);
            notiflu=(ImageView)itemView.findViewById(R.id.notiflu);
            itemnotification=(LinearLayout)itemView.findViewById(R.id.itemnotification);

        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
      /*  int position = holder.getAdapterPosition();
        if (mData.get(position) != null) {
            mData.get(position).getPlayer().release();
        }*/
        super.onViewRecycled(holder);
    }




}
