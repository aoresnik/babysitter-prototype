package xyz.aoresnik.babysitter.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class CommandMostUsedData {
    @JsonProperty(required = true)
    CommandData commandData;

    private Long usageCount;
}
