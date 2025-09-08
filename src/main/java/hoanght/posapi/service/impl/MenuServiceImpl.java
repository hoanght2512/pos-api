package hoanght.posapi.service.impl;

import hoanght.posapi.dto.menu.MenuResponse;
import hoanght.posapi.repository.jpa.ProductRepository;
import hoanght.posapi.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Cacheable("menus")
    public List<MenuResponse> listMenu() {
        return productRepository.findAllForMenus().stream().map((e) -> modelMapper.map(e, MenuResponse.class)).toList();
    }
}
