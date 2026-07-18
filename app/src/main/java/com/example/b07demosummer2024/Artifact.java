package com.example.b07demosummer2024;

public class Artifact implements java.io.Serializable {
    private String lotNumber;
    private String name;
    private String description;
    private String category;
    private String material;
    private String dynastyPeriod;
    private String imageUrl;
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;

    public Artifact(){}

    //getters
    public String getLotNumber() {
        return lotNumber;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }
    public String getDynastyPeriod() {
        return dynastyPeriod;
    }
    public String getMaterial() {
        return material;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getCulturalOrigin() {
        return culturalOrigin;
    }
    public String getDimensions() {
        return dimensions;
    }
    public String getConditionReport() {
        return conditionReport;
    }
    public String getCurrentLocation() {
        return currentLocation;
    }
    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }
    public String getProvenance() {
        return provenance;
    }
    public String getAccessionNumber() {
        return accessionNumber;
    }
    public String getNotes() {
        return notes;
    }

    //setters
    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public void setDynastyPeriod(String dynastyPeriod) {
        this.dynastyPeriod = dynastyPeriod;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setCulturalOrigin(String culturalOrigin) {
        this.culturalOrigin = culturalOrigin;
    }
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    public void setConditionReport(String conditionReport) {
        this.conditionReport = conditionReport;
    }
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }
    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
