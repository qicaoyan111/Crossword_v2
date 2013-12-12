package com.crossword.utils;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;

import com.crossword.data.Word;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

	public JsonUtil(){
		
	}
	
	public LinkedList<Word> parseJson(String json){
		Type listType = new TypeToken<LinkedList<Word>>(){}.getType();
		Gson gson = new Gson();
		LinkedList<Word> entries = gson.fromJson(json, listType);
		
		for(Iterator<Word> iterator = entries.iterator();iterator.hasNext();){
			Word word = (Word)iterator.next();
			entries.add(word);
		}
		return entries;
	}
	
	
}





























