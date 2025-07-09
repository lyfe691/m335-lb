package ch.wiss.m335_lb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import androidx.cardview.widget.CardView;

/**
 * main activity displaying the overview of all modules.
 * provides functionality to view, add and edit modules.
 */
public class MainActivity extends AppCompatActivity implements ModuleAdapter.OnModuleClickListener, ModuleAdapter.OnModuleLongClickListener {

    public static final String EXTRA_MODULE_ID = "module_id";
    
    private RecyclerView recyclerViewModules;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddModule;
    private CardView cardViewOverallAverage;
    private TextView textViewOverallAverage;
    
    private ModuleAdapter moduleAdapter;
    private ModuleStorage moduleStorage;
    private List<Module> modules;
    private ActivityResultLauncher<Intent> addEditModuleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setupActivityResultLauncher();
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        
        moduleStorage = new ModuleStorage(this);
        loadModules();
    }
    
    /**
     * sets up the activity result launcher for AddEditModuleActivty
     */
    private void setupActivityResultLauncher() {
        addEditModuleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // reload modules when returning from AddEditModuleActivity
                    loadModules();
                }
            }
        );
    }
    
    /**
     * init all view references
     */
    private void initializeViews() {
        recyclerViewModules = findViewById(R.id.recyclerViewModules);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        fabAddModule = findViewById(R.id.fabAddModule);
        cardViewOverallAverage = findViewById(R.id.cardViewOverallAverage);
        textViewOverallAverage = findViewById(R.id.textViewOverallAverage);
    }
    
    /**
     * sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        moduleAdapter = new ModuleAdapter();
        moduleAdapter.setOnModuleClickListener(this);
        moduleAdapter.setOnModuleLongClickListener(this);
        
        recyclerViewModules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewModules.setAdapter(moduleAdapter);
    }
    

    
    /**
     * sets up click listeners for interative elements
     */
    private void setupClickListeners() {
        fabAddModule.setOnClickListener(v -> openAddEditActivity(null));
    }
    
    /**
     * Loads modules from storage and updates the display.
     */
    private void loadModules() {
        modules = moduleStorage.loadModules();
        moduleAdapter.setModules(modules);
        updateEmptyState();
        updateOverallAverage();
    }
    
    /**
     * update the visibility of empty state message based on module count
     */
    private void updateEmptyState() {
        if (modules.isEmpty()) {
            recyclerViewModules.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewModules.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
    
    /**
     * calculates and displays the "overall" average grade of all modules.
     */
    private void updateOverallAverage() {
        List<Module> modulesWithGrades = modules.stream()
                .filter(Module::hasCompleteGrades)
                .collect(java.util.stream.Collectors.toList());
        
        if (modulesWithGrades.isEmpty()) {
            cardViewOverallAverage.setVisibility(View.GONE);
        } else {
            double totalAverage = modulesWithGrades.stream()
                    .mapToDouble(Module::getDurchschnittsnote)
                    .average()
                    .orElse(0.0);
            
            textViewOverallAverage.setText(String.format("%.1f", totalAverage));
            cardViewOverallAverage.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Oopens AddEditModuleActivity for creating new or editing existing module.
     * 
     * @param module module to edit, or nulll for creating new module
     */
    private void openAddEditActivity(Module module) {
        Intent intent = new Intent(this, AddEditModuleActivity.class);
        
        if (module != null) {
            intent.putExtra(EXTRA_MODULE_ID, module.getId());
        }
        
        addEditModuleLauncher.launch(intent);
    }
    
    @Override
    public void onModuleClick(Module module) {
        openAddEditActivity(module);
    }
    
    @Override
    public void onModuleLongClick(Module module) {
        showDeleteConfirmationDialog(module);
    }
    
    /**
     * shows confirmation dialog before deleting a module.
     * 
     * @param module module to delete
     */
    private void showDeleteConfirmationDialog(Module module) {
        new AlertDialog.Builder(this)
                .setTitle("Modul löschen")
                .setMessage("Möchten Sie das Modul \"" + module.getModulnummer() + " - " + module.getModultitel() + "\" wirklich löschen?")
                .setPositiveButton("Löschen", (dialog, which) -> deleteModule(module))
                .setNegativeButton("Abbrechen", null)
                .show();
    }
    
    /**
     * deletes the module and updates the display
     * 
     * @param moduleToDelete Module to del
     */
    private void deleteModule(Module moduleToDelete) {
        modules.removeIf(module -> module.getId().equals(moduleToDelete.getId()));
        moduleStorage.saveModules(modules);
        moduleAdapter.setModules(modules);
        updateEmptyState();
        updateOverallAverage();
    }
}