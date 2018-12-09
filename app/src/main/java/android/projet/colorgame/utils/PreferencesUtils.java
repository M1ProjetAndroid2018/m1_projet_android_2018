package android.projet.colorgame.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferencesUtils {

	private final static String PREFS_LOCATION = "colormatch_preferences";
	 
	private final static String KEY_SCORE = "score";
	private final static String KEY_TIME = "time";
	private final static String KEY_POSITIONS = "positions";
	
	private final static String KEY_BEST_SCORE = "bestScore";
	private final static String KEY_NAME_BEST_SCORE = "nameBS";
	
	public final String KEY_STATE = "state";
	public final String KEY_BEST_SCORES = "bestScores";
	
	private Context context;

	public PreferencesUtils(Context context){
		this.context = context;
	}

	public void setString(String key, String value){
		Editor editor = this.context.getSharedPreferences(PREFS_LOCATION, Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getString(String key, String defaultValue){
		SharedPreferences prefs = this.context.getSharedPreferences(PREFS_LOCATION, Context.MODE_PRIVATE);
		return prefs.getString(key, defaultValue);
	} 
	
	public String jsonSerializeBestScores(List<String> names, List<Integer> values){
		JSONArray jsonValues = new JSONArray();
		try {
		
	    if(names.size()==values.size())
	    	for (int i = 0; i < values.size(); i++) {
	    		JSONObject jsonBestScore = new JSONObject();
                
				jsonBestScore.put(KEY_BEST_SCORE, values.get(i));
				jsonBestScore.put(KEY_NAME_BEST_SCORE, names.get(i));
                jsonValues.put(jsonBestScore);
	    		
	    	}
		} catch (JSONException e) {
			
			return "";
		}
	    return jsonValues.toString();
	}
	
	public List<String> jsonDeserializeBestScoresNames(String json){
		 List<String> names = new ArrayList<String>();
		    if (json != null) {
		        try {
		            JSONArray array = new JSONArray(json);
		           
		            for (int i = 0; i < array.length(); i++) {
		                JSONObject jsonBestScore = new JSONObject();
		                jsonBestScore=array.getJSONObject(i);
		                String name = jsonBestScore.getString(KEY_NAME_BEST_SCORE);
		                names.add(name);
		            }
		        } catch (JSONException e) {
		           return new ArrayList<String>();
		        }
		    }
		    return names;
	}
	
	public List<Integer> jsonDeserializeBestScoresValues(String json){
		    
			 List<Integer> values = new ArrayList<Integer>();
			    if (json != null) {
			        try {
			            JSONArray array = new JSONArray(json);
			            for (int i = 0; i < array.length(); i++) {
			                JSONObject jsonBestScore = new JSONObject();
			                
			                jsonBestScore=array.getJSONObject(i);
			                int val;
			                val= jsonBestScore.getInt(KEY_BEST_SCORE);
			                values.add(val);
			            }
			        } catch (JSONException e) {
			           return new ArrayList<Integer>();
			        }
			    }
			    return values;
	}


	public String jsonSerializeState(int score, long time, int[] data ){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KEY_SCORE, score);
			jsonObject.put(KEY_TIME, time);
			
			JSONArray jsonValues = new JSONArray();
		    for (int i = 0; i < data.length; i++) {
		    	jsonValues.put(data[i]);
		    }
		    
		    jsonObject.put(KEY_POSITIONS, jsonValues);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	
		return jsonObject.toString();
	}

	public int jsonDeserializeScoreOfState(String json){
		if (json != null) {
	        try {
	            JSONObject jsonObj = new JSONObject(json);
	            return jsonObj.getInt(KEY_SCORE);
	        } catch (JSONException e) {
	           return -1;
	        }
	    }
	    return -1;
	}
	
	public long jsonDeserializeTimeOfState(String json){
		if (json != null) {
	        try {
	            JSONObject jsonObj = new JSONObject(json);
	            return jsonObj.getLong(KEY_TIME);
	        } catch (JSONException e) {
	           return 0;
	        }
	    }
	    return 0;
	}
	
	public int[] jsonDeserializePositionsOfState(String json){
		int[] positions=new int[140];
		if (json != null) {
	        try {
	            JSONObject jsonObj = new JSONObject(json);
	            JSONArray jsonPositions = jsonObj.getJSONArray(KEY_POSITIONS);
	    	    
	            if(jsonPositions.length()==positions.length){
	            	for (int i = 0; i < jsonPositions.length(); i++) {
	            		positions[i]=jsonPositions.getInt(i);
	                
	            	} 
	        	}
	           
	        }
	        catch (JSONException e) {
	          
	        }
	    }
	    return positions;
	}
}
