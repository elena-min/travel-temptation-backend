package org.individualproject.controller;

import org.individualproject.business.TrendingService;
import org.individualproject.domain.Excursion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trending-excursions")
public class TrendingController {

    private TrendingService trendingService;
    public TrendingController(TrendingService tService){
        this.trendingService = tService;
    }

    @GetMapping()
    public ResponseEntity<List<Excursion>> getTrendingExcursions(@RequestParam(name = "limit", defaultValue = "10") int limit)
    {
        List<Excursion> excursions = trendingService.getTrendingExcursion(limit);
        return ResponseEntity.ok().body(excursions);
    }
}
