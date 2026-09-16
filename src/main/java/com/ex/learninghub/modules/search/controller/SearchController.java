package com.ex.learninghub.modules.search.controller;

import com.ex.learninghub.modules.search.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final GlobalSearchService globalSearchService;

    @GetMapping
    public ResponseEntity<List<GlobalSearchService.SearchResultItem>> search(@RequestParam("q") String query) {
        return ResponseEntity.ok(globalSearchService.globalSearch(query));
    }
}
