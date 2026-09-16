package com.rentacar.Customer;

import com.rentacar.Customer.dto.CreateCustomerRequest;
import com.rentacar.Customer.dto.CustomerDetailResponse;
import com.rentacar.Customer.dto.CustomerSummaryResponse;
import com.rentacar.Customer.entity.CustomerType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

    @Mapping(target = "nationalIdMasked", source = "nationalId",
    qualifiedByName = "maskNationalId")
    CustomerDetailResponse toDetail(Customer customer);

    @Mapping(target = "displayName", source = ".")
    CustomerSummaryResponse toSummary(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blacklisted", ignore = true)
    @Mapping(target = "blacklistReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CreateCustomerRequest request);

    default String buildDisplayName(Customer c) {
        if(c.getType() == CustomerType.CORPORATE){
            return c.getCompanyName();
        }
        return c.getFirstName() + " " + c.getSurname();
    }

    @Named("maskNationalId")
    default String maskNationalId(String value) {
        if (value == null || value.length() != 11) return null;
        return value.substring(0,3) + "******" + value.substring(9);
    }


}
