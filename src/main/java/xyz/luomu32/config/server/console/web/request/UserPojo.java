package xyz.luomu32.config.server.console.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserPojo {

    @NotNull(groups = {CreateValid.class},
            message = "{user.role.id.not.null}")
    private Long roleId;

    @NotBlank(groups = {CreateValid.class, AuthValid.class}, message = "{user.name.not.blank}")
    private String name;
    @NotBlank(groups = {CreateValid.class, AuthValid.class}, message = "{user.password.not.blank}")
    private String password;


    public interface CreateValid {
    }

    public interface AuthValid {
    }
}
