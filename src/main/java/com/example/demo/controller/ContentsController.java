package com.example.demo.controller;

import com.example.demo.client.ContentsClient;

import com.example.demo.entity.Contents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ContentsController {

    @Autowired
    private ContentsClient contentsClient;

    @GetMapping("/movie")
    public String getMovieList(Model model) {
        List<Contents> contentsList = contentsClient.getAllContents();
        List<String> genres = List.of("thriler", "romance", "action", "comedy", "horror");
        System.out.println("전체 콘텐츠 수: " + contentsList.size());

        // 장르별 콘텐츠 리스트를 Map으로 분류
        Map<String, List<Contents>> contentsByGenre = new HashMap<>();
        for (String genre : genres) {
            List<Contents> filtered = contentsList.stream()
                    .filter(c -> genre.equalsIgnoreCase(c.getGenre()))
                    .collect(Collectors.toList());

            System.out.println("장르: " + genre + ", 콘텐츠 수: " + filtered.size());
            System.out.println("==> 저장 키: " + genre); // 여기 로그 확인

            contentsByGenre.put(genre, filtered);
        }
        System.out.println("=== 최종 contentsByGenre ===");
        contentsByGenre.forEach((k, v) -> 
            System.out.println("장르: " + k + ", 콘텐츠 수: " + v.size()));

        model.addAttribute("genres", genres);
        model.addAttribute("contentsByGenre", contentsByGenre);
        return "common/contents/MoviePage";
    }
    @GetMapping("/contents/form")
    public String addContentsForm() {
        return "admin/contents/addContentsForm";
    }

    @PostMapping("/contents/new")
public String registerContents(
        @RequestParam("title") String title,
        @RequestParam("director") String director,
        @RequestParam("year") int year,
        @RequestParam("kind") String kind,
        @RequestParam("genre") String genre,
        @RequestParam("running") int running,
        @RequestParam("summary") String summary,
        @RequestParam(value = "contentsFile", required = false) MultipartFile contentsFile
) {
    // 1. 파일 저장 또는 변환
    String fileUrl = null;
    if (contentsFile != null && !contentsFile.isEmpty()) {
        // TODO: 파일 저장 로직 (예: local, S3 등)
        fileUrl = "/uploads/" + contentsFile.getOriginalFilename();
        // 파일 저장하는 코드는 아래에 참고
    }

    // 2. Contents 객체 생성
    Contents contents = new Contents();
    contents.setTitle(title);
    contents.setDirector(director);
    contents.setYear(year);
    contents.setKind(kind);
    contents.setGenre(genre);
    contents.setRunning(running);
    contents.setSummary(summary);

    // 3. FeignClient로 전달
    contentsClient.createContents(contents);

    return "redirect:/"; // 목록 페이지로 리디렉션
}


    @PostMapping("/contents/delete")
    public String deleteContents(@RequestParam Long id) {
        contentsClient.deleteContents(id);
        return "redirect:/";
    }

    @PostMapping("/contents/modifyForm")
    public String getContentsModifyForm(@RequestParam Long id, Model model) {
        Contents content = contentsClient.getContentsById(id);
        model.addAttribute("contents", content);
        return "admin/contents/modifyContentsForm";
    }

    @PostMapping("/contents/modify")
    public String modifyContents(@ModelAttribute Contents contentForm,
                                 @RequestParam("contentsFile") MultipartFile files) {
        // MultipartFile은 FeignClient에서 바로 처리하기 어렵기 때문에 별도 처리 필요
        // 간단히 contentForm으로만 업데이트 호출 예시 (파일 업로드는 별도 API 필요할 수도 있음)
        contentsClient.updateContents(contentForm.getId(), contentForm);
        return "redirect:/";
    }

    @GetMapping("/contents/detail")
    public String getContentsDetail(@RequestParam Long contents_id, Model model) {
        Contents content = contentsClient.getContentsById(contents_id);
        model.addAttribute("content", content);
        return "common/contents/ContentsDetail";
    }

    @ResponseBody
    @PostMapping("/contents/checkPassword")
    public boolean checkPassword(@RequestParam Long contentid, @RequestParam String password) {
        // API에 맞게 check password 호출 로직 조정 필요 (현재 ContentsClient엔 해당 API 없음)
        // 예를 들어, 별도 API를 추가해야 할 수 있음
        return true; // 임시 처리
    }

}
