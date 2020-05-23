package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.repository.support.Querydsl4RepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {
    public MemberTestRepository(EntityManager entityManager) {
        super(entityManager, Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member)
                .fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                select(member)
                        .from(member)
                        .join(member.team, team)
                        .where(
                                nullableExpression(member.username::eq, condition.getUsername()),
                                nullableExpression(member.team.name::eq, condition.getTeamName()),
                                nullableExpression(member.age::goe, condition.getAgeGoe()),
                                nullableExpression(member.age::loe, condition.getAgeLoe())
                        ));
    }

    public Page<Member> searchPageByApplyPageCount(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(
                pageable,
                select(member)
                        .from(member)
                        .join(member.team, team)
                        .where(
                                nullableExpression(member.username::eq, condition.getUsername()),
                                nullableExpression(member.team.name::eq, condition.getTeamName()),
                                nullableExpression(member.age::goe, condition.getAgeGoe()),
                                nullableExpression(member.age::loe, condition.getAgeLoe())
                        ),
                select(member.id)
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(
                                nullableExpression(member.username::eq, condition.getUsername()),
                                nullableExpression(member.team.name::eq, condition.getTeamName()),
                                nullableExpression(member.age::goe, condition.getAgeGoe()),
                                nullableExpression(member.age::loe, condition.getAgeLoe())
                        )
        );
    }

    private BooleanExpression nullableExpression(Function<String, BooleanExpression> function, String value) {
        return hasText(value) ? function.apply(value) : null;
    }

    private BooleanExpression nullableExpression(Function<Integer, BooleanExpression> function, Integer value) {
        return value != null ? function.apply(value) : null;
    }
}
