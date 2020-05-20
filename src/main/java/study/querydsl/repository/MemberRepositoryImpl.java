package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
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

    private BooleanExpression nullableExpression(Function<String, BooleanExpression> function, String value) {
        return hasText(value) ? function.apply(value) : null;
    }

    private BooleanExpression nullableExpression(Function<Integer, BooleanExpression> function, Integer value) {
        return value != null ? function.apply(value) : null;
    }
}
