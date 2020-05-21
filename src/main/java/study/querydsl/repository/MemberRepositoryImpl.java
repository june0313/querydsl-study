package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .join(member.team, team)
                .where(
                        nullableExpression(member.username::eq, condition.getUsername()),
                        nullableExpression(member.team.name::eq, condition.getTeamName()),
                        nullableExpression(member.age::goe, condition.getAgeGoe()),
                        nullableExpression(member.age::loe, condition.getAgeLoe())
                )
                .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .join(member.team, team)
                .where(
                        nullableExpression(member.username::eq, condition.getUsername()),
                        nullableExpression(member.team.name::eq, condition.getTeamName()),
                        nullableExpression(member.age::goe, condition.getAgeGoe()),
                        nullableExpression(member.age::loe, condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryContents(condition, pageable);
        // 카운트 쿼리를 좀더 최적화 할 수 있는 경우 별도의 쿼리로 작성할 수 있다.
        long total = queryCount(condition);

        return new PageImpl<>(content, pageable, total);
    }

    private List<MemberTeamDto> queryContents(MemberSearchCondition condition, Pageable pageable) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .join(member.team, team)
                .where(
                        nullableExpression(member.username::eq, condition.getUsername()),
                        nullableExpression(member.team.name::eq, condition.getTeamName()),
                        nullableExpression(member.age::goe, condition.getAgeGoe()),
                        nullableExpression(member.age::loe, condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private long queryCount(MemberSearchCondition condition) {
        return queryFactory
                .select(member)
                .from(member)
                .join(member.team, team)
                .where(
                        nullableExpression(member.username::eq, condition.getUsername()),
                        nullableExpression(member.team.name::eq, condition.getTeamName()),
                        nullableExpression(member.age::goe, condition.getAgeGoe()),
                        nullableExpression(member.age::loe, condition.getAgeLoe())
                )
                .fetchCount();
    }

    private BooleanExpression nullableExpression(Function<String, BooleanExpression> function, String value) {
        return hasText(value) ? function.apply(value) : null;
    }

    private BooleanExpression nullableExpression(Function<Integer, BooleanExpression> function, Integer value) {
        return value != null ? function.apply(value) : null;
    }
}
