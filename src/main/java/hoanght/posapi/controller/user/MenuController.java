package hoanght.posapi.controller.user;

import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.menu.MenuResponse;
import hoanght.posapi.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/user/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<DataResponse<?>> listMenu() {
        List<MenuResponse> response = menuService.listMenu();
        return ResponseEntity.ok(DataResponse.success("List menu successfully", response));
    }
}
