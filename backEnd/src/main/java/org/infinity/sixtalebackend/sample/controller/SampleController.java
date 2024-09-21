package org.infinity.sixtalebackend.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    @GetMapping("/sample")
    public String sample(){
        return "테스트 페이지입니다!";
    }
}
