package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    @Getter(onMethod_ = @JsonProperty("id"))
    private Integer id;

    @Getter(onMethod_ = @JsonProperty("petId"))
    private Integer petId;

    @Getter(onMethod_ = @JsonProperty("quantity"))
    private Integer quantity;

    @Getter(onMethod_ = @JsonProperty("shipDate"))
    private String shipDate;

    @Getter(onMethod_ = @JsonProperty("status"))
    private String status;

    @Getter(onMethod_ = @JsonProperty("complete"))
    private boolean complete;

}
