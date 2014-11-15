package com.dnareader.system;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;
import com.dnareader.v0.R;

public class FragmentResultDebug extends Fragment {
	
	 int resultId;
	 Bitmap original;
	 Bitmap preProcessed;
	 String ocr;
	 private ImageView imageOriginal;
	 private ImageView imagePreProcessed;
	 private TextView ocrOutput;
	
	public static FragmentResultDebug newInstance(int num) {
		FragmentResultDebug f = new FragmentResultDebug();

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
         
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inSampleSize = 4;
         
         if(target.getImage() != null)
         original = BitmapFactory.decodeByteArray(target.getImage(),0,target.getImage().length,options);
         if(target.getPreProcessedimage() != null)
         preProcessed = BitmapFactory.decodeByteArray(target.getPreProcessedimage(),0,target.getPreProcessedimage().length,options);
         ocr = target.getOcrText();
     }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {   	   	
 
        View rootView = inflater.inflate(R.layout.fragment_result_debug, container, false);
        
        imageOriginal = (ImageView) rootView.findViewById(R.id.imageOriginal);    	
        imageOriginal.setImageBitmap(original);
        
        imagePreProcessed = (ImageView) rootView.findViewById(R.id.imagePreProcessed);    	
        imagePreProcessed.setImageBitmap(preProcessed);
        
        ocrOutput = (TextView) rootView.findViewById(R.id.ocrOutput);  
        ocrOutput.setText(ocr);
		
         
        return rootView;
    }
}