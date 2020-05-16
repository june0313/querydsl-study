package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        assertThat(memberJpaRepository.findById(member.getId())).containsSame(member);
        assertThat(memberJpaRepository.findAll()).containsExactly(member);
        assertThat(memberJpaRepository.findByUsername("member1")).containsExactly(member);
    }

    @Test
    void basicQueryDslTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        assertThat(memberJpaRepository.findById(member.getId())).containsSame(member);
        assertThat(memberJpaRepository.findAllQueryDsl()).containsExactly(member);
        assertThat(memberJpaRepository.findByUsernameQueryDsl("member1")).containsExactly(member);
    }
}