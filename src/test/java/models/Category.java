package models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Category {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;


}