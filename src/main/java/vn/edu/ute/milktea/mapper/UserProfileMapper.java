package vn.edu.ute.milktea.mapper;
import org.mapstruct.Mapper;
import vn.edu.ute.milktea.dto.UserProfileDto;
import vn.edu.ute.milktea.model.AppUser;
@Mapper(componentModel="spring")
public interface UserProfileMapper {
    UserProfileDto toDto(AppUser user);
}
