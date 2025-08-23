package hoanght.posapi.controller.user;

import hoanght.posapi.assembler.TableSessionAssembler;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.tablesession.AddProductsRequest;
import hoanght.posapi.dto.tablesession.SplitRequest;
import hoanght.posapi.dto.tablesession.TableSessionResponse;
import hoanght.posapi.model.OrderTable;
import hoanght.posapi.service.TableSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user/table-sessions")
@RequiredArgsConstructor
public class TableSessionController {
    private final TableSessionService tableSessionService;
    private final TableSessionAssembler tableSessionAssembler;

    @GetMapping
    public ResponseEntity<DataResponse<?>> getAllTableSessions(@PageableDefault Pageable pageable, PagedResourcesAssembler<OrderTable> pagedResourcesAssembler) {
        Page<OrderTable> tableSessions = tableSessionService.getAllTableSessions(pageable);
        PagedModel<TableSessionResponse> response = pagedResourcesAssembler.toModel(tableSessions, tableSessionAssembler);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<DataResponse<?>> getTableSessionById(@PathVariable Long tableId) {
        OrderTable tableSession = tableSessionService.getTableSessionById(tableId);
        TableSessionResponse response = tableSessionAssembler.toModel(tableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/{tableId}/orders")
    public ResponseEntity<DataResponse<?>> addOrdersToTableSession(@PathVariable Long tableId, @Valid @RequestBody AddProductsRequest orderRequest) {
        OrderTable updatedTableSession = tableSessionService.addOrderToTableSession(tableId, orderRequest);
        TableSessionResponse response = tableSessionAssembler.toModel(updatedTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/{tableId}/reserve")
    public ResponseEntity<DataResponse<?>> reserveTableSession(@PathVariable Long tableId) {
        OrderTable reservedTableSession = tableSessionService.reserveTableSession(tableId);
        TableSessionResponse response = tableSessionAssembler.toModel(reservedTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/change/{fromTableId}/{toTableId}")
    public ResponseEntity<DataResponse<?>> changeTableSession(@PathVariable Long fromTableId, @PathVariable Long toTableId) {
        OrderTable updatedTableSession = tableSessionService.changeTableSession(fromTableId, toTableId);
        TableSessionResponse response = tableSessionAssembler.toModel(updatedTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/merge/{fromTableId}/{toTableId}")
    public ResponseEntity<DataResponse<?>> mergeTableSessions(@PathVariable Long fromTableId, @PathVariable Long toTableId) {
        OrderTable mergedTableSession = tableSessionService.mergeTableSessions(fromTableId, toTableId);
        TableSessionResponse response = tableSessionAssembler.toModel(mergedTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/split/{fromTableId}/{toTableId}")
    public ResponseEntity<DataResponse<?>> splitTableSession(@PathVariable Long fromTableId, @PathVariable Long toTableId, @Valid @RequestBody SplitRequest splitRequest) {
        OrderTable splitTableSession = tableSessionService.splitTableSession(fromTableId, toTableId, splitRequest);
        TableSessionResponse response = tableSessionAssembler.toModel(splitTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/{tableId}/checkout")
    public ResponseEntity<DataResponse<?>> checkoutTableSession(@PathVariable Long tableId) {
        OrderTable checkedOutTableSession = tableSessionService.checkoutTableSession(tableId);
        TableSessionResponse response = tableSessionAssembler.toModel(checkedOutTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @PostMapping("/{tableId}/cancel")
    public ResponseEntity<DataResponse<?>> cancelTableSession(@PathVariable Long tableId) {
        OrderTable canceledTableSession = tableSessionService.cancelTableSession(tableId);
        TableSessionResponse response = tableSessionAssembler.toModel(canceledTableSession);
        return ResponseEntity.ok(DataResponse.success(response));
    }
}
