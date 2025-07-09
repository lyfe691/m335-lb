package ch.wiss.m335_lb;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Activity for adding new modules or editing existing ones.
 * Provides form validation and data persistence functionality.
 */
public class AddEditModuleActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutModulnummer;
    private TextInputLayout textInputLayoutModultitel;
    private TextInputLayout textInputLayoutNote1;
    private TextInputLayout textInputLayoutNote2;
    
    private TextInputEditText editTextModulnummer;
    private TextInputEditText editTextModultitel;
    private TextInputEditText editTextNote1;
    private TextInputEditText editTextNote2;
    
    private Button buttonSave;
    private Button buttonCancel;
    private Toolbar toolbar;
    
    private ModuleStorage moduleStorage;
    private Module currentModule;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_module);
        
        initializeViews();
        setupToolbar();
        setupClickListeners();
        
        moduleStorage = new ModuleStorage(this);
        checkEditMode();
    }
    
    /**
     * init all view references
     */
    private void initializeViews() {
        textInputLayoutModulnummer = findViewById(R.id.textInputLayoutModulnummer);
        textInputLayoutModultitel = findViewById(R.id.textInputLayoutModultitel);
        textInputLayoutNote1 = findViewById(R.id.textInputLayoutNote1);
        textInputLayoutNote2 = findViewById(R.id.textInputLayoutNote2);
        
        editTextModulnummer = findViewById(R.id.editTextModulnummer);
        editTextModultitel = findViewById(R.id.editTextModultitel);
        editTextNote1 = findViewById(R.id.editTextNote1);
        editTextNote2 = findViewById(R.id.editTextNote2);
        
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        toolbar = findViewById(R.id.toolbar);
    }
    
    /**
     * sets up toolbar with navigation.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    /**
     * sets up click listeners for buttons.
     */
    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> saveModule());
        buttonCancel.setOnClickListener(v -> finish());
    }
    
    /**
     * checks if activity was opened for editing existing module.
     */
    private void checkEditMode() {
        Long moduleId = getIntent().getLongExtra(MainActivity.EXTRA_MODULE_ID, -1);
        
        if (moduleId != -1) {
            isEditMode = true;
            loadModuleForEditing(moduleId);
            toolbar.setTitle("Modul bearbeiten");
        } else {
            toolbar.setTitle("Neues Modul");
        }
    }
    
    /**
     * loads existing module data for editing.
     */
    private void loadModuleForEditing(Long moduleId) {
        List<Module> modules = moduleStorage.loadModules();
        
        for (Module module : modules) {
            if (module.getId().equals(moduleId)) {
                currentModule = module;
                populateFields();
                break;
            }
        }
    }
    
    /**
     * populates form fields with current module data.
     */
    private void populateFields() {
        if (currentModule != null) {
            editTextModulnummer.setText(currentModule.getModulnummer());
            editTextModultitel.setText(currentModule.getModultitel());
            
            if (currentModule.getNote1() != null) {
                editTextNote1.setText(String.valueOf(currentModule.getNote1()));
            }
            if (currentModule.getNote2() != null) {
                editTextNote2.setText(String.valueOf(currentModule.getNote2()));
            }
        }
    }
    
    /**
     * val and saves the module.
     */
    private void saveModule() {
        if (validateInput()) {
            Module moduleToSave = createModuleFromInput();
            
            List<Module> modules = moduleStorage.loadModules();
            
            if (isEditMode) {
                // Update existing module
                for (int i = 0; i < modules.size(); i++) {
                    if (modules.get(i).getId().equals(currentModule.getId())) {
                        modules.set(i, moduleToSave);
                        break;
                    }
                }
            } else {
                // Add new module
                moduleToSave.setId(moduleStorage.getNextId());
                modules.add(moduleToSave);
            }
            
            moduleStorage.saveModules(modules);
            setResult(RESULT_OK);
            finish();
        }
    }
    
    /**
     * val  all input fields according to business rules.
     * 
     * @return true if all inputs are valid
     */
    private boolean validateInput() {
        clearErrors();
        boolean isValid = true;
        
        // val Modulnummer
        String modulnummer = editTextModulnummer.getText().toString().trim();
        if (TextUtils.isEmpty(modulnummer) || modulnummer.length() < 4) {
            textInputLayoutModulnummer.setError("Modulnummer muss mindestens 4 Zeichen haben");
            isValid = false;
        }
        
        // val Modultitel
        String modultitel = editTextModultitel.getText().toString().trim();
        if (TextUtils.isEmpty(modultitel) || modultitel.length() < 4) {
            textInputLayoutModultitel.setError("Modultitel muss mindestens 4 Zeichen haben");
            isValid = false;
        }
        
        // val Note1 (optional but must be valid number if provided)
        String note1Text = editTextNote1.getText().toString().trim();
        if (!TextUtils.isEmpty(note1Text)) {
            try {
                double note1 = Double.parseDouble(note1Text);
                if (note1 < 1.0 || note1 > 6.0) {
                    textInputLayoutNote1.setError("Note muss zwischen 1.0 und 6.0 liegen");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                textInputLayoutNote1.setError("Ungültige Zahl");
                isValid = false;
            }
        }
        
        // val Note2 (optional but must be valid number if provided)
        String note2Text = editTextNote2.getText().toString().trim();
        if (!TextUtils.isEmpty(note2Text)) {
            try {
                double note2 = Double.parseDouble(note2Text);
                if (note2 < 1.0 || note2 > 6.0) {
                    textInputLayoutNote2.setError("Note muss zwischen 1.0 und 6.0 liegen");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                textInputLayoutNote2.setError("Ungültige Zahl");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * clears all error messages from input fields.
     */
    private void clearErrors() {
        textInputLayoutModulnummer.setError(null);
        textInputLayoutModultitel.setError(null);
        textInputLayoutNote1.setError(null);
        textInputLayoutNote2.setError(null);
    }
    
    /**
     * creates Module object from current form input.
     * 
     * @return Module with form data
     */
    private Module createModuleFromInput() {
        Module module = new Module();
        
        if (isEditMode && currentModule != null) {
            module.setId(currentModule.getId());
        }
        
        module.setModulnummer(editTextModulnummer.getText().toString().trim());
        module.setModultitel(editTextModultitel.getText().toString().trim());
        
        // prse notes if provided
        String note1Text = editTextNote1.getText().toString().trim();
        if (!TextUtils.isEmpty(note1Text)) {
            try {
                module.setNote1(Double.parseDouble(note1Text));
            } catch (NumberFormatException e) {
                // Already val, should not happen
            }
        }
        
        String note2Text = editTextNote2.getText().toString().trim();
        if (!TextUtils.isEmpty(note2Text)) {
            try {
                module.setNote2(Double.parseDouble(note2Text));
            } catch (NumberFormatException e) {
                // Already val, should not happen
            }
        }
        
        return module;
    }
} 