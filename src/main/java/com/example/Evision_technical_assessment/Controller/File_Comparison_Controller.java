package com.example.Evision_technical_assessment.Controller;

import com.example.Evision_technical_assessment.Models.SimilarityResult;
import com.example.Evision_technical_assessment.Services.Cosine_Similarity_Service;
import com.example.Evision_technical_assessment.Services.Normal_Similarity_Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class File_Comparison_Controller {

    private final Cosine_Similarity_Service cosineSimilarityService;
    private final Normal_Similarity_Service normalSimilarityService;

    public File_Comparison_Controller(Cosine_Similarity_Service cosineSimilarityService , Normal_Similarity_Service normalSimilarityService) {
        this.cosineSimilarityService = cosineSimilarityService;
        this.normalSimilarityService = normalSimilarityService;
    }


    @GetMapping("/compareCosine")
    public List<SimilarityResult> getSimilaritiesCosine() {
        return cosineSimilarityService.getLastResults();
    }


    @GetMapping("/compareNormal")
    public List<SimilarityResult> getSimilaritiesNormal() {
        return normalSimilarityService.getLastResults();
    }
}