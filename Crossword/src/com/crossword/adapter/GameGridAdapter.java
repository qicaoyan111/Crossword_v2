/*
 * Copyright 2011 Alexis Lauper <alexis.lauper@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crossword.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.crossword.Crossword;
import com.crossword.R;
import com.crossword.activity.GameActivity;
import com.crossword.data.Module;
import com.crossword.data.Word;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GameGridAdapter extends BaseAdapter {

	private HashMap<Integer, TextView>	views = new HashMap<Integer, TextView>();
	private Context						context;
	private String[][]					area;			// Tableau repr茅sentant les lettres du joueur
	private String[][] 					displayArea;
	private String[][] 					correctionArea; // Tableau repr茅sentant les lettres correctes
	//boolean horizontal;
	private int 						displayHeight;
	private int 						width;
	private int 						height;
	public GameGridAdapter(Activity act, LinkedList<Word> entries, int width, int height,Module moudle)
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
		
		this.context = (Context)act;
		this.width = width;
		this.height = height;
		
		// Calcul area height
        Display display = act.getWindowManager().getDefaultDisplay();
        this.displayHeight = display.getWidth() / this.width;

        // Fill area and areaCorrection
        this.area = new String[this.height][this.width];
        this.correctionArea = new String[this.height][this.width];
        this.displayArea = new String[this.height][this.width];
	    for (Word entry: entries)
	    {
	    	String tmp = entry.getTmp();
	    	String text = entry.getCap();
	    	boolean horizontal = entry.getHoriz();
	    	int x = entry.getX();
	    	int y = entry.getY();
	    	
	    	for (int i = 0 ; i < entry.getLength(); i++) 
	    	{
	    		if (horizontal)
	    		{
	    			if (y >= 0 && y < this.height && x+i >= 0 && x+i < this.width)
	    			{
	    				this.area[y][x+i] = String.valueOf(tmp.charAt(i)).equals(Crossword.TMPSIGN)?Crossword.BLANK:String.valueOf(tmp.charAt(i));
	    				this.displayArea[y][x+i] = String.valueOf(tmp.charAt(i)).equals(Crossword.TMPSIGN)?Crossword.BLANK:String.valueOf(tmp.charAt(i));
	    				this.correctionArea[y][x+i] = String.valueOf(text.charAt(i));
	    			}
	    		}
	    		else
	    		{
	    			if (y+i >= 0 && y+i < this.height && x >= 0 && x < this.width)
	    			{
	    				this.area[y+i][x] =  String.valueOf(tmp.charAt(i)).equals(Crossword.TMPSIGN)?Crossword.BLANK:String.valueOf(tmp.charAt(i));
	    				this.displayArea[y+i][x] = String.valueOf(tmp.charAt(i)).equals(Crossword.TMPSIGN)?Crossword.BLANK:String.valueOf(tmp.charAt(i));
	    				this.correctionArea[y+i][x] = String.valueOf(text.charAt(i));
	    			}
	    		}
	    	}
	   }
    for(int currentX=0; currentX<this.width;currentX++)
    	for(int currentY=0;currentY<this.height;currentY++)
    		{
    		   if(isBlock(currentX,currentY))
    			  continue;
    		   if(area[currentY][currentX]==null)
    			   continue;
    		   Word currentWord = moudle.getWord(currentX,currentY,true);
    		  // currentWord.getHorizontal();
    		  
    			   
    			   if(moudle.isCorrect(moudle.getWord(currentWord.getX(), currentWord.getY(), currentWord.getHoriz()).getCap(),this.getWord(currentWord.getX(),currentWord.getY(),currentWord.getLength(), currentWord.getHoriz())))
       		    	{
       				  for(int l = 0; l < currentWord.getLength(); l++)
       				  {
       					if(currentWord.getHoriz())  this.setDisValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
       					       						
       						//gridAdapter.setValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
            		    if(!currentWord.getHoriz()) this.setDisValue(currentWord.getX(), currentWord.getY()+l,currentWord.getAns(l));  
       		            
       		        //    this.gridAdapter.notifyDataSetChanged();
       				  }
       		    	}
    		    if(this.isCross(currentX,currentY))
        		  {
    			   if(moudle.isCorrect(moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getCap(),
   						this.getWord(moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getX(),moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getY(), 
   								moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getLength(), 
   								moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getHoriz())))
	   		    	{
	   				  for(int l = 0; l < moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getLength(); l++)
	   				    {
	   						if(!currentWord.getHoriz()) 
	   						{
	   							this.setDisValue(moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getX()+l,moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getY(),moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getAns(l));
	   						  //  System.out.println("x:"+(currentX+l)+"y:"+currentY);
	   						}
	   						if(currentWord.getHoriz())
	   		            	{
	   		            	this.setDisValue(moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getX(),moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getY()+l,moudle.getWord(currentX, currentY, !currentWord.getHoriz()).getAns(l));  
	   		            	// System.out.println("x:"+currentX+"y:"+(currentY+l));
	   		            	}
	   		            }
	   		           // this.gridAdapter.notifyDataSetChanged();
	   			     }   
    	       }
    		}  		
    		  	      
	}
	
	@Override
	public int getCount() {
		return this.height * this.width;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		 
		TextView v = this.views.get(position);
		int y = (int)(position / this.width); 
		int x = (int)(position % this.width);
		String data = this.displayArea[y][x]!=Crossword.BLANK?this.displayArea[y][x]:" ";
		String correction = this.correctionArea[y][x];
		
		// Creation du composant
		if (v == null)
		{
			v = new TextView(context);
			v.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT, this.displayHeight));
			v.setTextSize((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4 ? 30 : 20);
			v.setGravity(Gravity.CENTER);

			if (data != null) {
				v.setBackgroundResource(R.drawable.area_empty);
				v.setTag(Crossword.AREA_WRITABLE);
			} else {
				v.setBackgroundResource(R.drawable.area_block1);
				v.setTag(Crossword.AREA_BLOCK);
			}
			
			this.views.put(position, v);
		}

		if(data!=null)//test
		{
				
					v.setTextColor(context.getResources().getColor(R.color.normal));//test
					v.setText(data.toUpperCase());
				//if((!data.matches("[a-zA-Z]"))&&(!data.equals(" "))){v.setTag(AREA_FISHED);}
		}
		return v;
	}

	public int getPercent() {
		int filled = 0;
		int empty = 0;
		
		for (int y = 0; y < this.height; y++)
			for (int x = 0; x < this.width; x++)
				if (this.area[y][x] != null) {
					if (this.area[y][x].equals(" "))
						empty++;
					else
						filled++;
				}
		return filled * 100 / (empty + filled);
	}

	public boolean isBlock(int x, int y) {
		return (this.area[y][x] == null);
	}

	public void setValue(int x, int y, String value) {
		if (this.area[y][x] != null)
			//this.isDraft=false;//test
		/*	this.area[y][x] = this.isDraft ? value.toLowerCase() : value.toUpperCase();*/
			{
			this.area[y][x] = value.toUpperCase();
			//this.displayArea[y][x] = value.toUpperCase();
			System.out.println(this.area[y][x]);
			}//test}
	}
	public void setDisValue(int x, int y, String value) {
		if (this.area[y][x] != null)
		
			{
		
			this.displayArea[y][x] = value.toUpperCase();
			
			System.out.println(this.displayArea[y][x]);
			}//test}
	}

	public String getWord(int x, int y, int length, boolean isHorizontal) {
    	StringBuffer word = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		if (isHorizontal) {
    			if (y < this.height && x+i < this.width)
    				word.append(this.area[y][x+i] != null ? this.area[y][x+i] : " ");
    		}
    		else {
    			if (y+i < this.height && x < this.width)
    				word.append(this.area[y+i][x] != null ? this.area[y+i][x] : " ");
    		}
    	}
    	return word.toString();
	}
/*
	public void setDraft(boolean value) {
		this.isDraft = value;
	}*/

	
	//判断是否是交叉点
	public boolean isCross(int x,int y){
		boolean c = false;
		String lD,rD,tD,bD;
		
        lD = x - 1<0?null:this.area[y][x - 1];
        rD = x + 1>=this.width?null:this.area[y][x + 1];
        tD = y - 1<0?null:this.area[y - 1][x];
        bD = y + 1>=this.height?null:this.area[y + 1][x];
		
        if((lD == null && rD == null) || (tD == null && bD == null)){
        	c = false;
        }else{
        	c = true;
        }
		return c;
	}
	
	
	//重新绘制，把小格的背景变为初始状态
	public void reDrawGridBackground(GridView v){
		
		for(int i = 0;i < this.height;i++){
			for(int j = 0;j < this.width;j++){
				
				int index = i*width + j;
				v.getChildAt(index).setBackgroundResource(area[i][j] == null?
						                                    R.drawable.area_block1:R.drawable.area_empty);
			}
		}
	}
	
	

}
