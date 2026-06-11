package flores.eternas.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColorFlorDTO {

    private Long id;
    private String descripcionColor;

    public ColorFlorDTO(Long id, String descripcionColor) {
        this.id = id;
        this.descripcionColor = descripcionColor;
    }
}
