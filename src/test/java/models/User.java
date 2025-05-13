package models;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Getter(onMethod_ = @JsonProperty("id"))
    private Long id;

    @Getter(onMethod_ = @JsonProperty("username"))
    private String username;

    @Getter(onMethod_ = @JsonProperty("firstName"))
    private String firstName;

    @Getter(onMethod_ = @JsonProperty("lastName"))
    private String lastName;

    @Getter(onMethod_ = @JsonProperty("email"))
    private String email;

    @Getter(onMethod_ = @JsonProperty("password"))
    private String password;

    @Getter(onMethod_ = @JsonProperty("phone"))
    private String phone;

    @Getter(onMethod_ = @JsonProperty("userStatus"))
    private int userStatus;

}
