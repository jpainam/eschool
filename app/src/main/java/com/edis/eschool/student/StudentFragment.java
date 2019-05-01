package com.edis.eschool.student;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edis.eschool.R;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.utils.Constante;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StudentFragment extends Fragment {
    private static final String TAG = "StudentFragment";
    AlertDialog dialog = null;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    public List<Student> studentList = new ArrayList<>();
    public String studentListJson = new String();
    public StudentRecyclerViewAdapter studentRecyclerViewAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean swipeEnabled = false;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StudentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static StudentFragment newInstance(int columnCount) {
        StudentFragment fragment = new StudentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        //if(savedInstanceState != null){
        //    studentListJson = savedInstanceState.getString("studentList");
         //   Type type = new TypeToken<List<Student>>(){}.getType();
        //    studentList = new Gson().fromJson(studentListJson, type);
        //    studentRecyclerViewAdapter.notifyDataSetChanged();
       // }
        if(dialog == null) {
            Log.e(TAG, "Instantiate dialog");
            dialog = new SpotsDialog.Builder()
                    .setContext(getContext()).setCancelable(true)
                    .setMessage(getString(R.string.loading_message))
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        // Set the adapter
        if (view instanceof SwipeRefreshLayout) {
            final Context context = view.getContext();
            swipeRefreshLayout = (SwipeRefreshLayout)view;
            recyclerView = view.findViewById(R.id.recyclerView);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            studentRecyclerViewAdapter = new StudentRecyclerViewAdapter(context,
                    studentList, mListener);
            recyclerView.setAdapter(studentRecyclerViewAdapter);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeEnabled = true;
                    doManualSyncAsync();
                }
            });
            // Configure the refreshing colors
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

        }
        return view;
    }
    public void doManualSyncAsync(){
        AccountManager accManager = AccountManager.get(getContext());
        Account account = accManager.getAccountsByType(Constante.ACCOUNT_TYPE)[0];
        if(account != null) {
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            settingsBundle.putBoolean(getString(R.string.SYNC_STATUS_ACTION), false);
            Log.i(TAG, "Start Sync Finish");
            ContentResolver.requestSync(account, Constante.AUTHORITY, settingsBundle);
            Log.i(TAG, "Manual Sync Finish");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(studentList.isEmpty()){
            initStudentListView();
        }
    }

    public void initStudentListView(){
        Log.i(TAG, "Running initStudentListView");
        studentList.clear();
        AsyncTask<URL, Integer, List<Student>> asyncTask = new AsyncTask<URL, Integer, List<Student>>() {
            @Override
            protected List<Student> doInBackground(URL... urls) {
                StudentDao dao = new StudentDao(getContext());
                return dao.getStudentList();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Running Dialog");
                dialog.show();
            }

            @Override
            protected void onPostExecute(List<Student> students) {
                studentList.addAll(students);
                studentRecyclerViewAdapter.notifyDataSetChanged();
                Gson gson = new Gson();
                studentListJson = gson.toJson(studentList);
                dialog.dismiss();
                Log.e(TAG, "Dismiss the dialog");
            }
        };
        asyncTask.execute();

        Log.i(TAG, "End InitStudentListView");
    }
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sAction = intent.getAction();
            if (getString(R.string.refresh_list_broadcast).equals(sAction) ) {
                // update the ListView here
                initStudentListView();
            }else if(sAction.equals(getString(R.string.SYNC_STATUS_ACTION))){
                String status = intent.getExtras().getString(getString(R.string.sync_status));
                Log.e(TAG, "status " + status);
                if(status.equals(getString(R.string.sync_running))){
                    Log.i(TAG, "Progress Bar Running");
                    if(!swipeEnabled){
                        dialog.show();
                    }
                }else if(status.equals(getString(R.string.sync_finished))){
                    Log.i(TAG, "Progress Bar Finished");
                    if(swipeEnabled){
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(context, getString(R.string.refreshed_message), Toast.LENGTH_SHORT).show();
                        swipeEnabled = false;
                    }else {
                        dialog.dismiss();
                    }
                }
            }
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        /**
         * Register the BroadcastReceiver e.g. in onAttach():
         */
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(getString(R.string.refresh_list_broadcast));
        myFilter.addAction(getString(R.string.SYNC_STATUS_ACTION));
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, myFilter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem refreshmenu = menu.findItem(R.id.refresh_menu_item);
        //refreshmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
         //   @Override
         //   public boolean onMenuItemClick(MenuItem item) {
        //
         //       return false;
         //   }
        //});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.refresh_menu_item:
                Log.i(TAG, "Refresh Menu Clicked");
                return false;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        /* unregister the */
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("studentList", studentListJson);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Student item);
    }
}
