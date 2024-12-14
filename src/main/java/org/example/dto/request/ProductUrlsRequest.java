package org.example.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUrlsRequest {
    @JsonProperty("domains")
    private ArrayList<String> domains;
}
