package ch.wiss.m335_lb;

/**
 * Data model representing a module with its number, title and optional grades.
 * Handles validation and grade calculation according to business requirements.
 */
public class Module {
    
    private Long id;
    private String modulnummer;
    private String modultitel;
    private Double note1;
    private Double note2;
    
    /**
     * Default constructor for creating empty module instances.
     */
    public Module() {
    }
    
    /**
     * Constructor for creating a new module with required fields.
     * 
     * @param modulnummer The module number (minimum 4 characters)
     * @param modultitel The module title (minimum 4 characters)
     */
    public Module(String modulnummer, String modultitel) {
        this.modulnummer = modulnummer;
        this.modultitel = modultitel;
    }
    
    /**
     * Full constructor for creating module with all data.
     */
    public Module(Long id, String modulnummer, String modultitel, Double note1, Double note2) {
        this.id = id;
        this.modulnummer = modulnummer;
        this.modultitel = modultitel;
        this.note1 = note1;
        this.note2 = note2;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getModulnummer() {
        return modulnummer;
    }
    
    public void setModulnummer(String modulnummer) {
        this.modulnummer = modulnummer;
    }
    
    public String getModultitel() {
        return modultitel;
    }
    
    public void setModultitel(String modultitel) {
        this.modultitel = modultitel;
    }
    
    public Double getNote1() {
        return note1;
    }
    
    public void setNote1(Double note1) {
        this.note1 = note1;
    }
    
    public Double getNote2() {
        return note2;
    }
    
    public void setNote2(Double note2) {
        this.note2 = note2;
    }
    
    /**
     * Calculates the average grade if both grades are present.
     * 
     * @return Average of note1 and note2, or null if incomplete
     */
    public Double getDurchschnittsnote() {
        if (hasCompleteGrades()) {
            return (note1 + note2) / 2.0;
        }
        return null;
    }
    
    /**
     * Validates that required fields meet minimum length requirements.
     * 
     * @return true if modulnummer and modultitel have at least 4 characters
     */
    public boolean isValid() {
        return modulnummer != null && modulnummer.trim().length() >= 4 &&
               modultitel != null && modultitel.trim().length() >= 4;
    }
    
    /**
     * Checks if both grades are present for calculating average.
     * 
     * @return true if both note1 and note2 are not null
     */
    public boolean hasCompleteGrades() {
        return note1 != null && note2 != null;
    }
    
    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", modulnummer='" + modulnummer + '\'' +
                ", modultitel='" + modultitel + '\'' +
                ", note1=" + note1 +
                ", note2=" + note2 +
                '}';
    }
} 