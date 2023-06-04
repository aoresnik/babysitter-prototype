package xyz.aoresnik.babysitter.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class CommandMostUsedData {
    CommandData commandData;

    private Long usageCount;
}
