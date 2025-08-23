package hoanght.posapi.controller.admin;

import hoanght.posapi.assembler.OrderTableAssembler;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.ordertable.OrderTableCreationRequest;
import hoanght.posapi.dto.ordertable.OrderTableResponse;
import hoanght.posapi.dto.ordertable.OrderTableUpdateRequest;
import hoanght.posapi.model.OrderTable;
import hoanght.posapi.service.OrderTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/admin/tables")
@RequiredArgsConstructor
public class OrderTableAdminController {
    private final OrderTableService orderTableService;
    private final OrderTableAssembler orderTableAssembler;

    @GetMapping
    public ResponseEntity<DataResponse<?>> findAllTables(@PageableDefault Pageable pageable, PagedResourcesAssembler<OrderTable> pagedResourcesAssembler) {
        Page<OrderTable> tables = orderTableService.findAll(pageable);
        PagedModel<OrderTableResponse> response = pagedResourcesAssembler.toModel(tables, orderTableAssembler);
        return ResponseEntity.ok(DataResponse.success("Tables retrieved successfully", response));
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<DataResponse<?>> findTableById(@PathVariable Long tableId) {
        OrderTable orderTable = orderTableService.findById(tableId);
        OrderTableResponse response = orderTableAssembler.toModel(orderTable);
        return ResponseEntity.ok(DataResponse.success("Table found successfully", response));
    }

    @PostMapping
    public ResponseEntity<Void> createTable(@Valid @RequestBody OrderTableCreationRequest orderTableCreationRequest) {
        OrderTable createdTable = orderTableService.create(orderTableCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(OrderTableAdminController.class).findTableById(createdTable.getId())).toUri()).build();
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<DataResponse<?>> updateTable(@PathVariable Long tableId, @Valid @RequestBody OrderTableUpdateRequest orderTableUpdateRequest) {
        OrderTable existingTable = orderTableService.update(tableId, orderTableUpdateRequest);
        OrderTableResponse response = orderTableAssembler.toModel(existingTable);
        return ResponseEntity.ok(DataResponse.success("Table updated successfully", response));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
        orderTableService.delete(tableId);
        return ResponseEntity.noContent().build();
    }
}
