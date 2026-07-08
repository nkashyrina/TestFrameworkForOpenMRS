package api.models;

import lombok.Data;

@Data
public class Link extends BaseModel {
    private String rel;
    private String uri;
    private String resourceAlias;
}
