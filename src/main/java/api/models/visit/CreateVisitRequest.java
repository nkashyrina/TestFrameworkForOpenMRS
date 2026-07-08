package api.models.visit;


import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateVisitRequest extends BaseModel {
    private String patient;
    private String visitType;
    private String startDatetime;
    private String stopDatetime;
    private String location;
    private String indication;
    private List<VisitAttribute> attributes;
    private List<String> encounters;
}
