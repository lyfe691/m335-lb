package ch.wiss.m335_lb;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for local storage of module data using SharedPreferences.
 * Handles serialization and persistence of module objects.
 */
public class ModuleStorage {
    
    private static final String PREFS_NAME = "ModulePrefs";
    private static final String MODULES_KEY = "modules";
    private static final String NEXT_ID_KEY = "next_id";
    
    private final SharedPreferences prefs;
    
    /**
     * Constructor initializing SharedPreferences for the given context.
     * 
     * @param context Application context for accessing SharedPreferences
     */
    public ModuleStorage(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Saves a list of modules to local storage.
     * 
     * @param modules List of modules to save
     */
    public void saveModules(List<Module> modules) {
        try {
            JSONArray jsonArray = new JSONArray();
            
            for (Module module : modules) {
                JSONObject jsonModule = moduleToJson(module);
                jsonArray.put(jsonModule);
            }
            
            prefs.edit()
                 .putString(MODULES_KEY, jsonArray.toString())
                 .apply();
                 
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads all modules from local storage.
     * 
     * @return List of stored modules, empty list if none found
     */
    public List<Module> loadModules() {
        List<Module> modules = new ArrayList<>();
        
        try {
            String jsonString = prefs.getString(MODULES_KEY, "[]");
            JSONArray jsonArray = new JSONArray(jsonString);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonModule = jsonArray.getJSONObject(i);
                Module module = jsonToModule(jsonModule);
                modules.add(module);
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return modules;
    }
    
    /**
     * Generates next unique ID for new modules.
     * 
     * @return Next available ID
     */
    public long getNextId() {
        long nextId = prefs.getLong(NEXT_ID_KEY, 1);
        prefs.edit().putLong(NEXT_ID_KEY, nextId + 1).apply();
        return nextId;
    }
    
    /**
     * Converts Module object to JSON representation.
     */
    private JSONObject moduleToJson(Module module) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", module.getId());
        json.put("modulnummer", module.getModulnummer());
        json.put("modultitel", module.getModultitel());
        
        if (module.getNote1() != null) {
            json.put("note1", module.getNote1());
        }
        if (module.getNote2() != null) {
            json.put("note2", module.getNote2());
        }
        
        return json;
    }
    
    /**
     * Converts JSON object back to Module instance.
     */
    private Module jsonToModule(JSONObject json) throws JSONException {
        Module module = new Module();
        module.setId(json.getLong("id"));
        module.setModulnummer(json.getString("modulnummer"));
        module.setModultitel(json.getString("modultitel"));
        
        if (json.has("note1")) {
            module.setNote1(json.getDouble("note1"));
        }
        if (json.has("note2")) {
            module.setNote2(json.getDouble("note2"));
        }
        
        return module;
    }
} 