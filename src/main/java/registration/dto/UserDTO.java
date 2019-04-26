package registration.dto;

import org.hibernate.validator.constraints.NotBlank;
import registration.model.User;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class UserDTO implements Serializable{
    private Integer id;
    @NotBlank(message = "name can't empty!")
    private String name;
    @NotBlank(message = "email can't empty!")
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public UserDTO(){

    }
    public UserDTO(User user){
        this.id=user.getId();
        this.name=user.getName();
        this.email=user.getEmail();
    }
    public UserDTO(RegistrationDTO user){
        this.id=user.getId();
        this.name=user.getName();
        this.email=user.getEmail();
    }
    //Test equal, override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return id.equals(userDTO.id) &&
                name.equals(userDTO.name) &&
                email.equals(userDTO.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,name,email);
    }
}
