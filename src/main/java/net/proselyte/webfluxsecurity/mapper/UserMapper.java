package net.proselyte.webfluxsecurity.mapper;

import net.proselyte.webfluxsecurity.dto.UserDTO;
import net.proselyte.webfluxsecurity.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map(UserDTO userDTO);
}
