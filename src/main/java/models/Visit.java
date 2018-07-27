package models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Alikin E.A. on 24.08.17.
 */
@Getter
@Setter
public class Visit extends Identificable {

    private Integer user;
    private Integer location;
    private Integer visited_at;
    private Integer mark;

}
