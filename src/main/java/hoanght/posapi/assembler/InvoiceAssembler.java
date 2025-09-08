package hoanght.posapi.assembler;

import hoanght.posapi.dto.invoice.InvoiceResponse;
import hoanght.posapi.model.Invoice;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class InvoiceAssembler extends RepresentationModelAssemblerSupport<Invoice, InvoiceResponse> {
    private final ModelMapper modelMapper;

    public InvoiceAssembler(ModelMapper modelMapper) {
        super(Invoice.class, InvoiceResponse.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public InvoiceResponse toModel(@NonNull Invoice invoice) {
        InvoiceResponse response = modelMapper.map(invoice, InvoiceResponse.class);
        response.setOrderTable(invoice.getOrderTable().getName());
        //        response.add(linkTo(methodOn(InvoiceAdminController.class).getInvoiceById(table.getId())).withSelfRel());
        return response;
    }
}
