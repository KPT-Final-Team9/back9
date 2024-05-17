package com.core.back9.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BuildingController {

    @GetMapping("/api/building")
    public String testController() {
        return "테스트 성공했다... 고생했다 ㅠㅠ";
    }
}
