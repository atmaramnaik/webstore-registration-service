package registration.exceptions;

import registration.dto.UserDTO;

import java.io.Serializable;
import java.util.Objects;

public class InputError extends RuntimeException implements Serializable {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    public static int DUPLICATE_EMAIL = 1;
    public static int DATA_TOO_LONG = 2;
    public static int UNKOWN = 0;
    public static int VALIDATION=3;

    public InputError(int code) {
        this.code = code;
    }

    public InputError(int code,String message){
        super(message);
        this.code=code;
    }

    //Test equal, override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputError error = (InputError) o;
        return this.code == error.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
