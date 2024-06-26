package com.refore.our.menu.controller;

import com.refore.our.menu.dto.MenuDto;
import com.refore.our.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;


    @PostMapping()
    public ResponseEntity<String> menuInsert(@RequestBody MenuDto menuDto){
        menuService.menuInsert(menuDto);
        return ResponseEntity.ok("등록");
    }



    @GetMapping()
    public ResponseEntity<String> menuIndex(@RequestBody MenuDto menuDto){
        menuService.menuIndex(menuDto);
        return ResponseEntity.ok("테스트");
    }



    @DeleteMapping()
    public ResponseEntity<String> menuDelete(@RequestBody MenuDto menuDto){
        menuService.menuDelete(menuDto);
        return ResponseEntity.ok("삭제");
    }


}
