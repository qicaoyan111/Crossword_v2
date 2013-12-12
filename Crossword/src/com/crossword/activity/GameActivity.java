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

package com.crossword.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;

import com.crossword.Crossword;
import com.crossword.CrosswordException;
import com.crossword.R;
import com.crossword.SAXFileHandler;
import com.crossword.keyboard.KeyboardView;
import com.crossword.keyboard.KeyboardViewInterface;
import com.crossword.parser.CrosswordParser;
import com.crossword.parser.GridParser;
import com.crossword.adapter.GameGridAdapter;
import com.crossword.data.Grid;
import com.crossword.data.Moudle;
import com.crossword.data.Word;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends CrosswordParentActivity implements OnTouchListener, KeyboardViewInterface {

	public enum GRID_MODE {NORMAL, CHECK, SOLVE};
	public GRID_MODE currentMode = GRID_MODE.NORMAL;
	
	private GridView 		gridView;
	private KeyboardView 	keyboardView;
	private GameGridAdapter gridAdapter;
	private TextView 		txtDescription;
	private Moudle			moudle;
	private TextView        txtDescriptionHor;
	private TextView        txtDescriptionVer;
	private TextView 		keyboardOverlay;
 
	private Grid			grid;
	private ArrayList<Word> entries;		// Liste des mots
	private ArrayList<View>	selectedArea = new ArrayList<View>(); // Liste des cases selectionn茅es

	private boolean			downIsPlayable;	// false si le joueur 脿 appuy茅 sur une case noire 
	private int 			downPos;		// Position ou le joueur 脿 appuy茅
    private int 			downX;			// Ligne ou le joueur 脿 appuy茅
    private int 			downY;			// Colonne ou le joueur 脿 appuy茅
    private int				lastX;          //上一次按下的位置X
    private int             lastY;          //上一次按下的位置Y
	private int 			currentPos;		// Position actuelle du curseur
	private int 			currentX;		// Colonne actuelle du curseur
	private int 			currentY;		// Ligne actuelle du curseur
	private Word			currentWord;	// Mot actuellement selectionn茅
	private Word            currentWordHor;
	private Word            currentWordVer;
	private boolean 		horizontal;		// Sens de la selection
    private boolean 		isCross;        //判断是否是交叉点
	private String 			filename;		// Nom de la grille

	private boolean 		solidSelection;	// PREFERENCES: Selection persistante
	private boolean			gridIsLower;	// PREFERENCES: Grille en minuscule
	
	private int width;
	private int height;

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crossword, menu);
        return true;
    }
  /*  
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	MenuItem itemCheck = menu.findItem(R.id.menu_check);
    	MenuItem itemSolve = menu.findItem(R.id.menu_solve);
    	itemCheck.setIcon(preferences.getBoolean("grid_check", false) ? R.drawable.ic_menu_check_enable : R.drawable.ic_menu_check);
    	itemSolve.setIcon(currentMode == GRID_MODE.SOLVE ? R.drawable.ic_menu_solve_enable : R.drawable.ic_menu_solve);
		return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (item.getItemId()) {
        case R.id.menu_check:
    		boolean checked = !preferences.getBoolean("grid_check", false);
    		preferences.edit().putBoolean("grid_check", checked).commit();
        	if (currentMode != GRID_MODE.SOLVE) {
        		currentMode = checked ? GRID_MODE.CHECK : GRID_MODE.NORMAL;
        	}
        	this.gridAdapter.notifyDataSetChanged();
        	return true;
        case R.id.menu_solve:
        	if (currentMode == GRID_MODE.SOLVE)
        		currentMode = (preferences.getBoolean("grid_check", false) ? GRID_MODE.CHECK : GRID_MODE.NORMAL);
        	else
        		currentMode = GRID_MODE.SOLVE;
        	this.gridAdapter.notifyDataSetChanged();
        	return true;
        case R.id.menu_grid:
        	Intent intent = new Intent(this, GridActivity.class);
    		intent.putExtra("grid", grid);
        	startActivity(intent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	*/
	@Override
	public void onPause()
	{
		save();
		super.onPause();
	}
	/*
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode) {
    	case Crossword.REQUEST_PREFERENCES:
    		if (Crossword.DEBUG) Toast.makeText(this, "PREFERENCES_UPDATED", Toast.LENGTH_SHORT).show();
    		readPreferences();
    		this.gridAdapter.setLower(this.gridIsLower);
    		this.gridAdapter.notifyDataSetChanged();
        	break;
    	}
	}

	private void readPreferences() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.solidSelection = preferences.getBoolean("solid_selection", false);
		this.gridIsLower = preferences.getBoolean("grid_is_lower", false);
		if (currentMode != GRID_MODE.SOLVE)
			currentMode = preferences.getBoolean("grid_check", false) ? GRID_MODE.CHECK : GRID_MODE.NORMAL;
		
	}
*/
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.game);
	    moudle = new Moudle();
		//readPreferences();	    
		//开始根据点击的关卡获取获取到的文件名称，来找到相应的xml文件并且解析
	    try {
		    //this.filename = getIntent().getExtras().getString("filename");
		    
	    	this.filename = "basic002.xml";
			File file = new File(String.format(Crossword.GRID_LOCAL_PATH, this.filename));
			//Toast.makeText(this, String.format(Crossword.GRID_LOCAL_PATH, this.filename), Toast.LENGTH_SHORT).show();
			if (file.exists())
			{
				// Get grid meta informations (name, author, date, level)
				GridParser gridParser = new GridParser();
				SAXFileHandler.read((DefaultHandler)gridParser, String.format(Crossword.GRID_LOCAL_PATH, this.filename));
				this.grid = gridParser.getData();
			    if (this.grid == null) {
			    	finish();
			    	return;
			    }

			    this.entries= moudle.getentry(this.filename);
			    if (this.entries == null) {
			    	finish();
			    	return;
			    }
			}
			else
			{
				
		    	finish();
		    	
		    	return;
			}
		} catch (CrosswordException e) {
			
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	    
	    this.width = this.grid.getWidth();
	    this.height = this.grid.getHeight();
        this.lastX = -1;
        this.lastY = -1;
	    Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        int keyboardHeight = (int)(height / 4.4);
        this.txtDescriptionHor = (TextView)findViewById(R.id.description_horizotal);
        this.txtDescriptionVer = (TextView)findViewById(R.id.description_vertical);
        this.gridView = (GridView)findViewById(R.id.grid);
        this.gridView.setOnTouchListener(this);
        this.gridView.setNumColumns(this.width);
        android.view.ViewGroup.LayoutParams gridParams = this.gridView.getLayoutParams();
        gridParams.height = height - keyboardHeight - this.txtDescriptionHor.getLayoutParams().height;
        this.gridView.setLayoutParams(gridParams);
        this.gridView.setVerticalScrollBarEnabled(false);
		this.gridAdapter = new GameGridAdapter(this, this.entries, this.width, this.height,moudle);
		this.gridView.setAdapter(this.gridAdapter);

        this.keyboardView = (KeyboardView)findViewById(R.id.keyboard);
        this.keyboardView.setDelegate(this);
        android.view.ViewGroup.LayoutParams KeyboardParams = this.keyboardView.getLayoutParams();
        KeyboardParams.height = keyboardHeight;
        this.keyboardView.setLayoutParams(KeyboardParams);

        this.keyboardOverlay = (TextView)findViewById(R.id.keyboard_overlay);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
            	int position = this.gridView.pointToPosition((int)event.getX(), (int)event.getY());
            	View child = this.gridView.getChildAt(position);

            	// Si pas de mot sur cette case (= case noire), aucun traitement
            	if (child == null || child.getTag().equals(GameGridAdapter.AREA_BLOCK)) {
            		if (this.solidSelection == false) {
                        clearSelection();
                    	this.gridAdapter.notifyDataSetChanged();
            		}
            			
            		this.downIsPlayable = false;
            		return true;
            	}
        		this.downIsPlayable = true;

            	// Stocke les coordonnees d'appuie sur l'ecran
            	this.downPos = position;
                this.downX = this.downPos % this.width;
                this.downY = this.downPos / this.width;
                System.out.println("ACTION_DOWN, x:" + this.downX + ", y:" + this.downY + ", position: " + this.downPos);

                clearSelection();


            	this.gridAdapter.notifyDataSetChanged();
        		break;
            }

            case MotionEvent.ACTION_UP:
            {
            	// Si le joueur 脿 appuy茅 sur une case noire, aucun traitement 
            	if (this.downIsPlayable == false)
            		return true;
            	
                int position = this.gridView.pointToPosition((int)event.getX(), (int)event.getY());
                int x = position % this.width;
                int y = position / this.width;
                System.out.println("ACTION_DOWN, x:" + x + ", y:" + y + ", position: " + position);
                //判断输入方向
                
                this.horizontal = (lastY == y && Math.abs(lastX - x)>0 || (lastX == -1 && lastY == -1))?true:false;
                this.horizontal = (lastX == x && Math.abs(lastY - y)>0)?false:true;
                this.lastX = x;//获取上一次的横向位置
                this.lastY = y;//获取上一次的纵向位置
                this.currentX = x;
            	this.currentY = y;
            	this.isCross = this.gridAdapter.isCross(currentX, currentY);
            	currentWord = moudle.getWord(currentX,currentY,this.horizontal);
              
        	    if (this.currentWord == null)
        	    	break;
        	    this.horizontal = this.currentWord.getHorizontal();
        	  //在设置背景之前先重绘一遍
        		this.gridAdapter.reDrawGridBackground(this.gridView);
                if(isCross){
                	
                	this.currentWordHor = moudle.getWord(x, y, true);
                	this.currentWordVer = moudle.getWord(x, y, false);
                	this.setWordBackground(this.currentWordHor, x, y);
                	this.setWordBackground(this.currentWordVer, x, y);
                }else{
                	this.setWordBackground(currentWord, x, y);
                }
                
                
                this.setDescription(currentWordHor, currentWordVer, currentWord);
        	    this.gridAdapter.notifyDataSetChanged();
        	    break;
            }
        }
        // if you return false, these actions will not be recorded
        return true;
	}
	
	// Remet les anciennes case selectionnees dans leur etat normal
    private void clearSelection() {
    	for (View selected: selectedArea)
    		selected.setBackgroundResource(R.drawable.area_empty);
    	selectedArea.clear();
	}
/*
	private Word getWord(int x, int y, boolean horizontal)
    {
        Word horizontalWord = null;
        Word verticalWord = null;
     //   Word nullWord=
        //System.out.println(horizontal);
	    for (Word entry: this.entries) {
	    	if (x >= entry.getX() && x <= entry.getXMax())
	    		if (y >= entry.getY() && y <= entry.getYMax()) {
	    			System.out.println("entry.getHorizontal()..."+entry.getHorizontal());
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
*/
	@Override
	public void onKeyDown(String value, int location[], int width) {
		System.out.println("onKeyDown: " + value + ", insert in: " + currentX + "x" + currentY);

		// Deplace l'overlay du clavier
		if (value.equals(" ") == false) {
			int offsetX = (this.keyboardOverlay.getWidth() - width) / 2;
			int offsetY = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Crossword.KEYBOARD_OVERLAY_OFFSET, getResources().getDisplayMetrics());
			FrameLayout.LayoutParams lp = (LayoutParams)this.keyboardOverlay.getLayoutParams();
			lp.leftMargin = location[0] - offsetX;
			lp.topMargin = location[1] - offsetY;
			this.keyboardOverlay.setLayoutParams(lp);
			this.keyboardOverlay.setText(value);
			this.keyboardOverlay.clearAnimation();
			this.keyboardOverlay.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onKeyUp(String value) {
		System.out.println("onKeyUp: " + value + ", insert in: " + currentX + "x" + currentY);

		// Efface l'overlay du clavier
		if (value.equals(" ") == false) {
			this.keyboardOverlay.setAnimation(AnimationUtils.loadAnimation(this, R.anim.keyboard_overlay_fade_out));
			this.keyboardOverlay.setVisibility(View.INVISIBLE);
		}

		// Si aucun mot selectionne, retour
		if (this.currentWord == null)
			return;

		// Case actuelle
		int x = this.currentX;
		int y = this.currentY;
        
		// Si la case est noire => retour
		if (this.gridAdapter.isBlock(x, y))
			return;
		
		// Ecrit la lettre sur le "curseur"
		this.gridAdapter.setValue(x, y, value);
		this.gridAdapter.setDisValue(x, y,value);
		this.gridAdapter.notifyDataSetChanged();
		
		//moudle.rightDisplay(this.currentWord,this.currentX, this.currentY, gridAdapter,this.isCross);
		if(moudle.isCorrect(moudle.getWord(this.currentWord.getX(), this.currentWord.getY(), this.currentWord.getHorizontal()).getText(),gridAdapter.getWord(this.currentWord.getX(),this.currentWord.getY(),this.currentWord.getLength(), this.currentWord.getHorizontal())))
	    {
			  for(int l = 0; l < this.currentWord.getLength(); l++)
			  {
				if(this.currentWord.getHorizontal())  
				{
					gridAdapter.setDisValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
					//gridAdapter.setValue(currentWord.getX()+l, currentWord.getY(),currentWord.getAns(l));
				}
	            if(!this.currentWord.getHorizontal()) gridAdapter.setDisValue(currentWord.getX(), currentWord.getY()+l,currentWord.getAns(l));  
	            
	            this.gridAdapter.notifyDataSetChanged();
			  }
    	}
		if(this.isCross)
		{		
		
			if(moudle.isCorrect(moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getText(),
					gridAdapter.getWord(moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getX(),moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getY(), 
							moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getLength(), 
							moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getHorizontal())))
	    	{
			  for(int l = 0; l < moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getLength(); l++)
			    {
					if(!this.currentWord.getHorizontal())  {gridAdapter.setDisValue(moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getX()+l,moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getY(),moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getAns(l));
					 System.out.println("x:"+(currentX+l)+"y:"+currentY);
				}
	            if(this.currentWord.getHorizontal())
	            {
	            	gridAdapter.setDisValue(moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getX(),moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getY()+l,moudle.getWord(this.currentX, this.currentY, !currentWord.getHorizontal()).getAns(l));  
	            	 System.out.println("x:"+currentX+"y:"+(currentY+l));
	            }
	      
	            this.gridAdapter.notifyDataSetChanged();
			  }
    	    }			
		}
		//this.gridAdapter.notifyDataSetChanged();
		// Deplace sur le "curseur" sur la case precendante (effacer), ou suivante (lettres)
		if (value.equals(" ")) {
			x = (this.horizontal ? x - 1 : x);
			y = (this.horizontal ? y: y - 1);
		}
		else
		{
			x = (this.horizontal ? x + 1 : x);
			y = (this.horizontal ? y: y + 1);
		}
		
		// Si la case suivante est disponible, met la case en jaune, remet l'ancienne en bleu, et set la nouvelle position
		if (x >= 0 && x < this.width
				&& y >= 0 && y < this.height
				&& this.gridAdapter.isBlock(x,y) == false) {
			//this.gridView.getChildAt(y * this.width + x).setBackgroundResource(R.drawable.area_current);
			//this.gridView.getChildAt(this.currentY * this.width + this.currentX).setBackgroundResource(R.drawable.area_selected);
			this.currentX = x;
			this.currentY = y;
		}
		
		
		this.isCross = this.gridAdapter.isCross(currentX, currentY);
       
		currentWord = moudle.getWord(currentX,currentY,this.horizontal);
		  
          
          this.horizontal = this.currentWord.getHorizontal();
        //在设置背景之前先重绘一遍
        this.gridAdapter.reDrawGridBackground(this.gridView);
        if(this.isCross){
        	
        	this.currentWordHor = moudle.getWord(currentX, currentY, true);
        	this.currentWordVer = moudle.getWord(currentX, currentY, false);
        	
        	this.setWordBackground(this.currentWordHor, currentX, currentY);
        	this.setWordBackground(this.currentWordVer, currentX, currentY);
        }else{
        	this.setWordBackground(currentWord, currentX, currentY);
        }
        
        
        this.setDescription(currentWordHor, currentWordVer, currentWord);
        
		
	}
	
	
	
	
	/*有问题，怎么添加进去，直接考虑json吧
	 * */
    private void save() {
		// writre new XML file
    	// moudle.save()

    	StringBuffer wordHorizontal = new StringBuffer();
    	StringBuffer wordVertical = new StringBuffer();
	    for (Word entry: this.entries) {
	    	int x = entry.getX();
	    	int y = entry.getY();
    	    String word = String.format(
    	    		"<word  x=\"%d\" y=\"%d\" description=\"%s\" tmp=\"%s\" >%s</word>\n",
    	    		x,
    	    		y,
    	    		entry.getDescription(),
    	    		this.gridAdapter.getWord(x, y, entry.getLength(), entry.getHorizontal()),
    	    		//entry.getAns()
    	    		entry.getText());
    	    if (entry.getHorizontal())
    	    	wordHorizontal.append(word);
    	    else
    	    	wordVertical.append(word);
	    }

    	StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<grid>\n");
		sb.append("<name>" + grid.getName() + "</name>\n");
		sb.append("<description>" + grid.getDescription() + "</description>\n");
		if (this.grid.getDate() != null)
			sb.append("<date>" + new SimpleDateFormat("dd/MM/yyyy").format(grid.getDate()) + "</date>\n");
		sb.append("<author>" + grid.getAuthor() + "</author>\n");
		sb.append("<level>" + grid.getLevel() + "</level>\n");
		sb.append("<percent>" + gridAdapter.getPercent()+"</percent>\n");
		sb.append("<width>" + this.width + "</width>\n");
		sb.append("<height>" + this.height + "</height>\n");
		sb.append("<horizontal>\n");
		sb.append(wordHorizontal);
		sb.append("</horizontal>\n");
		sb.append("<vertical>\n");
		sb.append(wordVertical);
		sb.append("</vertical>\n");
		sb.append("</grid>\n");
		
		// Make directory if not exists
		File directory = new File(Crossword.GRID_DIRECTORY);
		if (directory.exists() == false)
			directory.mkdir();
		
		// Write XML
		FileWriter file;
		try {
			file = new FileWriter(String.format(Crossword.GRID_LOCAL_PATH, this.filename));
			file.write(sb.toString());
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(sb);
	}
    

	/*@Override
	public void setDraft(boolean value) {
		this.gridAdapter.setDraft(value);
	}
*/
	

	
	
  
	
	//设置横向或纵向词对应的小格背景
	public void setWordBackground(Word word,int currX,int currY){
		
		int x = word.getX();
		int y = word.getY();
		boolean horizontal = word.getHorizontal();
		int currIndex = currY*this.width + currX;
	
		for(int l = 0;l<word.getLength();l++){
			int index = y*this.width + x + l*(horizontal?1:this.width);
			View currentChild = this.gridView.getChildAt(index);
			if(currentChild != null){
				currentChild.setBackgroundResource(index == currIndex?R.drawable.area_current:R.drawable.area_selected);
			    selectedArea.add(currentChild);
			}
		}
	}
	
	
	
	//设置描述信息
	public void setDescription(Word currentWordHor,Word currentWordVer,Word currentWord){//设置提示信息
		  String descriptionHor = isCross?"横向:"+this.currentWordHor.getDescription():
              (this.horizontal?"横向:"+currentWord.getDescription():"");
          String descriptionVer = isCross?"纵向:"+this.currentWordVer.getDescription():
              (this.horizontal?"":"纵向:"+currentWord.getDescription());

          this.txtDescriptionHor.setText(descriptionHor);
          this.txtDescriptionVer.setText(descriptionVer);
		 
	}
	/*
	  private boolean isCorrect(String currentWords,String correctWords)
	    {
	    	System.out.println(currentWords);
	    	System.out.println(correctWords);
	    	System.out.println(currentWords.equalsIgnoreCase(correctWords)==true ?true:false);
	    	
	    	return  currentWords.equalsIgnoreCase(correctWords)==true ?true:false;
	    }
	*/
	
}
