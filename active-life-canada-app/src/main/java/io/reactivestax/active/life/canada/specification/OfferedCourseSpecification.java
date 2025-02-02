package io.reactivestax.active.life.canada.specification;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OfferedCourseSpecification {
    public static Specification<OfferedCourse> hasCourseName(String courseName) {
        return ((root, query, criteriaBuilder) -> courseName == null || courseName.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ShortConstant.COURSE).get(ShortConstant.NAME)),
                getWildCardSearchCriteria(courseName)
        ));
    }

    public static Specification<OfferedCourse> hasStartDate(LocalDate startDate) {
        return ((root, query, criteriaBuilder) -> startDate == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get(ShortConstant.START_DATE), startDate));
    }

    public static Specification<OfferedCourse> hasEndDate(LocalDate endDate) {
        return ((root, query, criteriaBuilder) -> endDate == null ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get(ShortConstant.END_DATE), endDate));
    }

    public static Specification<OfferedCourse> hasCity(String city) {
        return ((root, query, criteriaBuilder) -> city == null || city.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ShortConstant.FACILITY).get(ShortConstant.CITY)),
                getWildCardSearchCriteria(city)
        ));
    }

    public static Specification<OfferedCourse> hasProvince(String province) {
        return ((root, query, criteriaBuilder) -> province == null || province.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ShortConstant.FACILITY).get(ShortConstant.PROVINCE)),
                getWildCardSearchCriteria(province)
        ));
    }

    public static Specification<OfferedCourse> hasCategory(String category) {
        return ((root, query, criteriaBuilder) -> category == null || category.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ShortConstant.COURSE).get(ShortConstant.SUB_CATEGORY)
                        .get(ShortConstant.CATEGORY).get(ShortConstant.NAME)), getWildCardSearchCriteria(category)
        ));
    }

    public static Specification<OfferedCourse> hasSubCategory(String subCategory) {
        return ((root, query, criteriaBuilder) -> subCategory == null || subCategory.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get(ShortConstant.COURSE).get(ShortConstant.SUB_CATEGORY)
                        .get(ShortConstant.NAME)), getWildCardSearchCriteria(subCategory)
        ));
    }

    public static Specification<OfferedCourse> hasAgeGroup(String ageGroup) {
        return ((root, query, criteriaBuilder) -> ageGroup == null || ageGroup.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.equal(
                criteriaBuilder.lower(root.get(ShortConstant.COURSE).get(ShortConstant.AGE_GROUP)
                        .get(ShortConstant.NAME)), ageGroup.toLowerCase()
        ));
    }

    private static String getWildCardSearchCriteria(String criteria) {
        return "%" + criteria.toLowerCase() + "%";
    }
}
