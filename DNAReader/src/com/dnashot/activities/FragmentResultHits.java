package com.dnashot.activities;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.dnashot.data.Hit;
import com.dnashot.data.Result;
import com.dnashot.system.HitsAdapter;
import com.dnashot.system.ResultManager;
import com.dnashot.v0.R;

public class FragmentResultHits extends Fragment {
	
	int resultId;
	private ExpandableListView content;
	private HitsAdapter adapter;
	List<Hit> hits;	
	
	public static FragmentResultHits newInstance(int num) {
		FragmentResultHits f = new FragmentResultHits();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
	
	 @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         resultId = getArguments() != null ? getArguments().getInt("num") : 1;
         Result target = MainActivity.listResults.get(resultId); 
         target.setHits(ResultManager.loadHits(getActivity(), target.getLongId()));         
         hits = target.getHits();        
     }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_result_hits, container, false);
        content = (ExpandableListView) rootView.findViewById(R.id.expandable_list);
        
        adapter = new HitsAdapter(getActivity(), hits);
		content.setAdapter(adapter);
         
        return rootView;
    }
}