package dto;

import models.Model;

/**
 * Represents the ability to be converted to a Model and back
 * @param <T> the type to be returned
 */
public interface DTO<T extends Model>
{
     /**
      * Obtain the corresponding DTO for the current model.
      * @return a DTO, from dto.vault or dto.user
      */
     T toModel() throws Exception;
}
