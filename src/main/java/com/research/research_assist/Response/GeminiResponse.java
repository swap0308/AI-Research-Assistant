package com.research.research_assist.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.research.research_assist.Response.Candidate;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}
