package com.research.research_assist.Controller;

import com.research.research_assist.Request.ResearchRequest;
import com.research.research_assist.Service.ResearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
public class ResearchController {
    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processData(@RequestBody ResearchRequest researchRequest){
        String res = researchService.processContent(researchRequest);

        return ResponseEntity.ok(res);
    }
}
