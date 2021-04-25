package com.n26.controllers;

import com.n26.models.Statistics;
import com.n26.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller that handles statistics GET calls
 */
@RestController
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<Statistics> statistics() {
        return new ResponseEntity<>(statisticsService.get(), HttpStatus.OK);
    }
}
