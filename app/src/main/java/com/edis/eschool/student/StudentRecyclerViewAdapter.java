package com.edis.eschool.student;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.edis.eschool.R;

import com.edis.eschool.dummy.DummyContent.DummyItem;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.utils.Constante;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link StudentFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class StudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentRecyclerViewAdapter.ViewHolder> {

    private final List<Student> mValues;
    private final StudentFragment.OnListFragmentInteractionListener mListener;
    private Context context;
    public StudentRecyclerViewAdapter(Context context, List<Student> items, StudentFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Student st = mValues.get(position);
        holder.mItem = st;
        holder.mStudentName.setText(mValues.get(position).getLastName());
        if(st.getPhoto() == "" || st.getPhoto() == null){
            if(st.equals(Constante.MALE)) {
                holder.mIconView.setImageResource(R.drawable.male_avatar);
            }else{
                   holder.mIconView.setImageResource(R.drawable.female_avatar);
            }
        }else {

            //ContextWrapper cw = new ContextWrapper(context);
            //File directory = cw.getDir(Constante.IMG_DIR, Context.MODE_PRIVATE);
            //File studentPhoto = new File(directory, st.getPhoto());
            //Picasso.with(context).load(studentPhoto).into(holder.mIconView);
            Uri uri = Uri.parse(st.getPhoto());
            holder.mIconView.setImageURI(uri);
        }
        //holder.mStudentName.setText(mValues.get(position).id);
        holder.mStudentNotifs.setText((int)(Math.random()*10) + "");
        holder.mStudentDescription.setText(st.getEtablissement());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView mIconView;
        public final TextView mStudentName;
        public final TextView mStudentDate;
        public final TextView mStudentNotifs;
        public final TextView mStudentDescription;

        public Student mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIconView = (SimpleDraweeView) view.findViewById(R.id.student_icon);
            mStudentName = (TextView) view.findViewById(R.id.student_name);
            mStudentDate = (TextView) view.findViewById(R.id.student_date);
            mStudentNotifs = (TextView) view.findViewById(R.id.student_notifs);
            mStudentDescription = (TextView) view.findViewById(R.id.student_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStudentDescription.getText() + "'";
        }
    }
}
