package artifixal.reimbursementcalculationapp.daos;

import java.util.Optional;

/**
 *
 * @author ArtiFixal
 * @param <T>
 */
public class OptionalDBField<T>{
    protected Optional<T> field;
    protected String columnName;

    public OptionalDBField(Optional<T> field, String columnName) {
        this.field = field;
        this.columnName = columnName;
    }

    public Optional<T> getField() {
        return field;
    }
    
    public String getColumnName() {
        return columnName;
    }
}
