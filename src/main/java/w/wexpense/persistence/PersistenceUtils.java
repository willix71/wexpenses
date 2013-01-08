package w.wexpense.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.OneToMany;

public class PersistenceUtils {
   
   /**
    * Finds the property's "mappedBy" value.
    * 
    * @param entityClass
    *            the entityClass containing the property
    * @param propertyName
    *            the name of the property to find the "mappedBy" value for.
    * @return the value of mappedBy in an annotation.
    */
   public static String getMappedByProperty(Class<?> entityClass, Object propertyName) {
       OneToMany otm = getAnnotationForProperty(OneToMany.class, entityClass, propertyName.toString());
       if (otm != null && !"".equals(otm.mappedBy())) {
           return otm.mappedBy();
       }
       // Fall back on convention
       return entityClass.getSimpleName().toLowerCase();
   }

   /**
    * Finds a given annotation on a property.
    * 
    * @param annotationType
    *            the annotation to find.
    * @param entityClass
    *            the class declaring the property
    * @param propertyName
    *            the name of the property for which to find the annotation.
    * @return the annotation
    */
   public static <A extends Annotation> A getAnnotationForProperty(
           Class<A> annotationType, Class<?> entityClass, String propertyName) {
       A annotation = getAnnotationFromField(annotationType, entityClass, propertyName);
       if (annotation == null) {
           annotation = getAnnotationFromPropertyGetter(annotationType, entityClass, propertyName);
       }
       return annotation;
   }

   /**
    * Finds a given annotation on a field.
    */
   public static <A extends Annotation> A getAnnotationFromField(
           Class<A> annotationType, Class<?> entityClass, String propertyName) {
       Field field = null;
       try {
           // TODO: get fields from @mappedsuperclasses as well.
           field = entityClass.getDeclaredField(propertyName);
       } catch (Exception e) {
           // Field not found
       }

       if (field != null && field.isAnnotationPresent(annotationType)) {
           return field.getAnnotation(annotationType);
       }
       
       return null;
   }
   
   /**
    * Finds a given annotation on a property getter method.
    */
   public static <A extends Annotation> A getAnnotationFromPropertyGetter(
           Class<A> annotationType, Class<?> entityClass, String propertyName) {
       // TODO: support for private getters? -> need to recursively search
       // superclasses as well.
       
   	Method getter = null;
       try {
           getter = entityClass.getMethod("get"
                   + propertyName.substring(0, 1).toUpperCase()
                   + propertyName.substring(1));
       } catch (Exception e) {
           // Try isXXX
           try {
               getter = entityClass.getMethod("is"
                       + propertyName.substring(0, 1).toUpperCase()
                       + propertyName.substring(1));
           } catch (Exception e1) {
               // No getter found.
           }
       }
       if (getter != null && getter.isAnnotationPresent(annotationType)) {
           return getter.getAnnotation(annotationType);
       }
       return null;
   }
}
