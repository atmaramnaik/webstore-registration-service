package registration.dto;

import org.hibernate.validator.constraints.NotBlank;
import registration.model.User;

import java.io.Serializable;
import java.util.Objects;

public class RegistrationDTO implements Serializable {
    private Integer id;
    @NotBlank(message = "name can't empty!")
    private String name;
    @NotBlank(message = "email can't empty!")
    private String email;

    @NotBlank(message = "password can't empty!")
    private String password;

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
    public RegistrationDTO(){

    }
    public RegistrationDTO(User user){
        this.id=user.getId();
        this.name=user.getName();
        this.email=user.getEmail();
        this.password=user.getPassword();
    }
    //Test equal, override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationDTO registrationDTO = (RegistrationDTO) o;
        return id.equals(registrationDTO.id) &&
                name.equals(registrationDTO.name) &&
                email.equals(registrationDTO.email) &&
                password.equals(registrationDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,name,email,password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
