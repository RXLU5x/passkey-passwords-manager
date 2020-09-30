package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.DTO;

/**
 * Represents the ability to be converted to a DTO and back.
 * @param <T> the type to be returned
 */
public interface Model<T extends DTO>
{
    Gson json = new GsonBuilder()
        .setExclusionStrategies(new HiddenAnnotationExclusionStrategy())
        .create();

    /**
     * Obtain the corresponding DTO for the current model.
     * @return a DTO, from dto.vault or dto.user
     */
    T toDTO();

    default <C extends DTO> String toJsonHidden(C dto) {
        return json.toJson(dto, dto.getClass());
    }
}
