package com.example.demo.client;

import java.util.List;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Contents;

@FeignClient(name = "contents-api", url = "http://netflixclone-service:8082")
public interface ContentsClient {

    @GetMapping("/api/contents")
    List<Contents> getAllContents();

    @GetMapping("/api/contents/{id}")
    Contents getContentsById(@PathVariable("id") Long id);

    @PostMapping("/api/contents/json")
    Contents createContents(@RequestBody Contents contents);

    @PutMapping("/api/contents/{id}")
    Contents updateContents(@PathVariable("id") Long id, @RequestBody Contents contents);

    @DeleteMapping("/api/contents/{id}")
    void deleteContents(@PathVariable("id") Long id);
}
