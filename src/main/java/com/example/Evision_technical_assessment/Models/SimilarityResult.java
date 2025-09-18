package com.example.Evision_technical_assessment.Models;

public class SimilarityResult {
    private final String fileName;
    private final double score;

    public SimilarityResult(String fileName, double score) {
        this.fileName = fileName;
        this.score = score;
    }

    public String getFileName() { return fileName; }
    public double getScore() { return score; }
}
