package models;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private String[] photoUrls;
    private Tag[] tags;
    private String status;


    @JsonProperty("id")
    public Long getId() { return id; }

    @JsonProperty("category")
    public Category getCategory() { return category; }

    @JsonProperty("name")
    public String getName() { return name; }

    @JsonProperty("photoUrls")
    public String[] getPhotoUrls() { return photoUrls; }

    @JsonProperty("tags")
    public Tag[] getTags() { return tags; }

    @JsonProperty("status")
    public String getStatus() { return status; }

}



