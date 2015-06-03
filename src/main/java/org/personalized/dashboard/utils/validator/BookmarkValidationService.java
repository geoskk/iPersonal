package org.personalized.dashboard.utils.validator;

import com.google.common.collect.Lists;
import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.validator.constraints.NotEmpty;
import org.personalized.dashboard.FieldKeys;
import org.personalized.dashboard.model.Bookmark;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

/**
 * Created by sudan on 26/5/15.
 */
public class BookmarkValidationService implements ValidationService<Bookmark> {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Override
    public List<ErrorEntity> validate(Bookmark bookmark) {
        List<ErrorEntity> errorEntities = Lists.newArrayList();
        validateConstraints(bookmark, errorEntities);
        validateUrl(bookmark, errorEntities);


        return errorEntities;
    }

    private void validateUrl(Bookmark bookmark, List<ErrorEntity> errorEntities) {
        UrlValidator urlValidator = new UrlValidator();
        if(!urlValidator.isValid(bookmark.getUrl())) {
            ErrorEntity errorEntity = new ErrorEntity(ErrorCodes.INVALID_URL.name(), ErrorCodes.INVALID_URL.getDescription(), FieldKeys.BOOKMARK_URL);
            errorEntities.add(errorEntity);
        }
    }

    private void validateConstraints(Bookmark bookmark, List<ErrorEntity> errorEntities) {

        Field fields [] = Bookmark.class.getDeclaredFields();
        for(Field field : fields) {
            Set<ConstraintViolation<Bookmark>> constraintViolations = validator.validateProperty(bookmark, field.getName());
            for(ConstraintViolation<Bookmark> constraintViolation : constraintViolations) {
                String keyName = field.getAnnotation(FieldName.class).name();
                if(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType() == NotEmpty.class ||
                        constraintViolation.getConstraintDescriptor().getAnnotation().annotationType() == NotNull.class) {
                    ErrorEntity errorEntity = new ErrorEntity(ErrorCodes.EMPTY_FIELD.name(),
                            MessageFormat.format(ErrorCodes.EMPTY_FIELD.getDescription(), constraintViolation.getPropertyPath().toString()), keyName);
                    errorEntities.add(errorEntity);
                }
                else if(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType() == Size.class) {
                    ErrorEntity errorEntity = new ErrorEntity(ErrorCodes.LENGTH_EXCEEDED.name(),
                            MessageFormat.format(ErrorCodes.LENGTH_EXCEEDED.getDescription(), constraintViolation.getPropertyPath().toString(),
                                    constraintViolation.getConstraintDescriptor().getAttributes().get("max")), keyName);
                    errorEntities.add(errorEntity);
                }
            }
        }
    }
}
