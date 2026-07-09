package com.agritech.user.mapper;

import com.agritech.user.dto.response.AddressResponse;
import com.agritech.user.dto.response.UserProfileResponse;
import com.agritech.user.entity.UserAddress;
import com.agritech.user.entity.UserProfile;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting {@link UserProfile} and {@link UserAddress} entities
 * to their corresponding response DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    /**
     * Map a {@link UserProfile} entity to a {@link UserProfileResponse} DTO.
     * The {@code role} field is mapped as a String using the enum's name().
     *
     * @param profile the source entity
     * @return the mapped response DTO
     */
    @Mapping(target = "role", expression = "java(profile.getRole().name())")
    UserProfileResponse toResponse(UserProfile profile);

    /**
     * Map a {@link UserAddress} entity to an {@link AddressResponse} DTO.
     * The {@code isDefault} boolean field is mapped from the entity's {@code default} property.
     *
     * @param address the source entity
     * @return the mapped response DTO
     */
    @Mapping(target = "isDefault", source = "default")
    AddressResponse toAddressResponse(UserAddress address);
}
