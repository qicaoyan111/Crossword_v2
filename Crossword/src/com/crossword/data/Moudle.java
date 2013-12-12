package com.crossword.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.DefaultHandler;

import com.crossword.Crossword;
import com.crossword.CrosswordException;
import com.crossword.SAXFileHandler;
import com.crossword.parser.CrosswordParser;

public class Moudle {
	 private ArrayList<Word> entries;		// Liste des mots
	// private Grid			grid;	
	 public ArrayList<Word> getentry(String filename) throws CrosswordException{
		 	//ArrayList<Word> entries;
			 CrosswordParser crosswordParser = new CrosswordParser();
			SAXFileHandler.read((DefaultHandler)crosswordParser, String.format(Crossword.GRID_LOCAL_PATH, filename));
			this.entries = crosswordParser.getData();		
	
		  return this.entries;}
	  
	  public boolean isCorrect(String currentWords,String correctWords)
	    {	    	
	    	return  currentWords.equalsIgnoreCase(correctWords)==true ?true:false;
	    }
	  
	  public Word getWord(int x, int y, boolean horizontal)
	    {
	        Word horizontalWord = null;
	        Word verticalWord = null;
	     //   Word nullWord=
	        //System.out.println(horizontal);
		    for (Word entry: this.entries) {
		    	if (x >= entry.getX() && x <= entry.getXMax())
		    		if (y >= entry.getY() && y <= entry.getYMax()) {
		    			//System.out.println("entry.getHorizontal()..."+entry.getHorizontal());
	        	    	if (entry.getHorizontal())
	        	    		        	    		
	        	    			horizontalWord = entry;
	        	    		
	        	    	else
	        	    		verticalWord = entry;
		    		}
		    }
		    if (horizontal)
		    {
		    	System.out.println((horizontalWord != null) ? horizontalWord : verticalWord);
		    	return (horizontalWord != null) ? horizontalWord : verticalWord;}
		    else
		    	return (verticalWord != null) ? verticalWord : horizontalWord;
	    }
	/*  
	  public void save()
	  {
			StringBuffer wordHorizontal = new StringBuffer();
	    	StringBuffer wordVertical = new StringBuffer();
	    	try
	    	{
	    		 JSONObject gameboard = new JSONObject();  
	    		 gameboard.put("file", "td.json");  
	    		 gameboard.put("uniqueid", "123456");
	    		 gameboard.put("vol", 1);
	    		 gameboard.put("level", 0);
	    		 gameboard.put("category", "Ê«´Ê");
	    		 JSONArray wordList = new JSONArray();  
	    		 JSONObject words = new JSONObject();  
	    		 for (Word entry: this.entries) {
	 		    	int x = entry.getX();
	 		    	int y = entry.getY();
	 		    	//int 
	 	  
	 		    }
	    		 
	    		 
	    		 words.put();
	    	}
	    	catch(JSONException ex)
	    	{
	    		throw new RuntimeException(ex);  
	    	}

		  
		    
	  } */
	/*  
	public void rightDisplay(Word currentWord,int currentX,int currentY,GameGridAdapter gridAdapter,boolean isCross)
	  {
		  if(this.isCorrect(this.getWord(currentWord.getX(), currentWord.getY(), currentWord.getHorizontal()).getText(),gridAdapter.getWord(currentWord.getX(),currentWord.getY(),currentWord.getLength(), currentWord.getHorizontal())))
		    {
				  for(int l = 0; l < currentWord.getLength(); l++)
				  {
					if(currentWord.getHorizontal())  
					{
						gridAdapter.setDisValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
						//gridAdapter.setValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
					}
		            if(!currentWord.getHorizontal()) gridAdapter.setDisValue(currentWord.getX(), currentWord.getY()+l,currentWord.getAns(l));  
		            
		           // gridAdapter.notifyDataSetChanged();
				  }
	    	}
			if(isCross)
			{		
			
				if(this.isCorrect(this.getWord(currentX, currentY, !currentWord.getHorizontal()).getText(),
						gridAdapter.getWord(this.getWord(currentX, currentY, !currentWord.getHorizontal()).getX(),this.getWord(currentX,currentY, !currentWord.getHorizontal()).getY(), 
								this.getWord(currentX, currentY, !currentWord.getHorizontal()).getLength(), 
								this.getWord(currentX, currentY, !currentWord.getHorizontal()).getHorizontal())))
		    	{
				  for(int l = 0; l < this.getWord(currentX, currentY, !currentWord.getHorizontal()).getLength(); l++)
				    {
						if(!currentWord.getHorizontal())  {gridAdapter.setDisValue(this.getWord(currentX,currentY, !currentWord.getHorizontal()).getX()+l,this.getWord(currentX, currentY, !currentWord.getHorizontal()).getY(),this.getWord(currentX, currentY, !currentWord.getHorizontal()).getAns(l));
						 System.out.println("x:"+(currentX+l)+"y:"+currentY);
					}
		            if(currentWord.getHorizontal())
		            {
		            	gridAdapter.setDisValue(this.getWord(currentX, currentY, !currentWord.getHorizontal()).getX(),this.getWord(currentX, currentY, !currentWord.getHorizontal()).getY()+l,this.getWord(currentX, currentY, !currentWord.getHorizontal()).getAns(l));  
		            	 System.out.println("x:"+currentX+"y:"+(currentY+l));
		            }
		      
		          
				  }
	    	    }
			} 
			gridAdapter.notifyDataSetChanged();
	  }
	  */
/*	  public String getWord(int x, int y, int length, boolean isHorizontal) {
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
		}*/
}
