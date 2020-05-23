package study.querydsl.repository.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Getter(value = AccessLevel.PROTECTED)
public abstract class Querydsl4RepositorySupport {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final Querydsl querydsl;

    public Querydsl4RepositorySupport(EntityManager entityManager, Class<?> domainClass) {
        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
        EntityPath path = resolver.createPath(entityInformation.getJavaType());

        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.querydsl = new Querydsl(entityManager, new PathBuilder<>(path.getType(), path.getMetadata()));
    }

    protected <T> JPAQuery<T> select(Expression<T> expr) {
        return getQueryFactory().select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(EntityPath<T> expr) {
        return getQueryFactory().selectFrom(expr);
    }

    protected <T> Page<T> applyPagination(Pageable pageable, JPAQuery<T> query) {
        List<T> content = getQuerydsl().applyPagination(pageable, query).fetch();
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    protected <T, C> Page<T> applyPagination(Pageable pageable, JPAQuery<T> contentsQuery, JPAQuery<C> countQuery) {
        List<T> content = getQuerydsl().applyPagination(pageable, contentsQuery).fetch();
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }
}
