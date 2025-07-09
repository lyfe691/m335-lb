package ch.wiss.m335_lb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying modules in a list
 * handles module data binding and click events.
 */
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    
    private List<Module> modules;
    private OnModuleClickListener clickListener;
    private OnModuleLongClickListener longClickListener;
    
    /**
     * interface for handling module item clicks
     */
    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
    
    /**
     * interface for handling module item long clicks
     */
    public interface OnModuleLongClickListener {
        void onModuleLongClick(Module module);
    }
    
    /**
     * constructor initializing empty module list
     */
    public ModuleAdapter() {
        this.modules = new ArrayList<>();
    }
    
    /**
     * sets the click listener for module items
     * 
     * @param listener listener to handle module clicks
     */
    public void setOnModuleClickListener(OnModuleClickListener listener) {
        this.clickListener = listener;
    }
    
    /**
     * sets the long click listener for module items.
     * 
     * @param listener listener to handle module long clicks
     */
    public void setOnModuleLongClickListener(OnModuleLongClickListener listener) {
        this.longClickListener = listener;
    }
    
    /**
     * updates the module list and refreshes the display.
     * 
     * @param newModules new list of modules
     */
    public void setModules(List<Module> newModules) {
        this.modules = newModules;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = modules.get(position);
        holder.bind(module);
    }
    
    @Override
    public int getItemCount() {
        return modules.size();
    }
    
    /**
     * ViewHolder class for module items
     * handles data binding and click events for individual module cads.
     */
    class ModuleViewHolder extends RecyclerView.ViewHolder {
        
        private final TextView textViewModulNumber;
        private final TextView textViewModulTitle;
        private final TextView textViewGradeInfo;
        
        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewModulNumber = itemView.findViewById(R.id.textViewModulNumber);
            textViewModulTitle = itemView.findViewById(R.id.textViewModulTitle);
            textViewGradeInfo = itemView.findViewById(R.id.textViewGradeInfo);
            
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onModuleClick(modules.get(position));
                    }
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onModuleLongClick(modules.get(position));
                        return true;
                    }
                }
                return false;
            });
        }
        
        /**
         * binds module data to the view elements.
         * 
         * @param module module to display
         */
        public void bind(Module module) {
            textViewModulNumber.setText(module.getModulnummer());
            textViewModulTitle.setText(module.getModultitel());
            
            // Display grade information
            if (module.hasCompleteGrades()) {
                Double average = module.getDurchschnittsnote();
                textViewGradeInfo.setText(String.format("Durchschnitt: %.1f", average));
            } else if (module.getNote1() != null || module.getNote2() != null) {
                textViewGradeInfo.setText("Noten: noch nicht komplett");
            } else {
                textViewGradeInfo.setText("Noch keine Noten eingetragen");
            }
        }
    }
} 