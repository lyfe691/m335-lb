package ch.wiss.m335_lb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Main activity displaying the overview of all modules.
 * Provides functionality to view, add and edit modules.
 */
public class MainActivity extends AppCompatActivity implements ModuleAdapter.OnModuleClickListener {

    public static final String EXTRA_MODULE_ID = "module_id";
    
    private RecyclerView recyclerViewModules;
    private TextView textViewEmpty;
    private FloatingActionButton fabAddModule;
    
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
     * Sets up the activity result launcher for AddEditModuleActivity.
     */
    private void setupActivityResultLauncher() {
        addEditModuleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Reload modules when returning from AddEditModuleActivity
                    loadModules();
                }
            }
        );
    }
    
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        recyclerViewModules = findViewById(R.id.recyclerViewModules);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        fabAddModule = findViewById(R.id.fabAddModule);
    }
    
    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        moduleAdapter = new ModuleAdapter();
        moduleAdapter.setOnModuleClickListener(this);
        
        recyclerViewModules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewModules.setAdapter(moduleAdapter);
    }
    
    /**
     * Sets up click listeners for interactive elements.
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
    }
    
    /**
     * Updates visibility of empty state message based on module count.
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
     * Opens AddEditModuleActivity for creating new or editing existing module.
     * 
     * @param module Module to edit, or null for creating new module
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
}