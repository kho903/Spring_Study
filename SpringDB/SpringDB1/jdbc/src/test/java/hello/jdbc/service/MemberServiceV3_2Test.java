package hello.jdbc.service;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;

/**
	 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
class MemberServiceV3_2Test {

	public static final String Member_A = "memberA";
	public static final String Member_B = "memberEx";
	public static final String Member_EX = "ex";

	private MemberRepositoryV3 memberRepository;
	private MemberServiceV3_2 memberService;

	@BeforeEach
	void before() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		memberRepository = new MemberRepositoryV3(dataSource);

		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		memberService = new MemberServiceV3_2(transactionManager, memberRepository);
	}

	@AfterEach
	void after() throws SQLException {
		memberRepository.delete(Member_A);
		memberRepository.delete(Member_B);
		memberRepository.delete(Member_EX);
	}

	@Test
	@DisplayName("정상 이체")
	public void accountTransfer() throws SQLException {
		// given
		Member memberA = new Member(Member_A, 10000);
		Member memberB = new Member(Member_B, 10000);
		memberRepository.save(memberA);
		memberRepository.save(memberB);

		// when
		log.info("START TX");
		memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
		log.info("END TX");

		// then
		Member findMemberA = memberRepository.findById(memberA.getMemberId());
		Member findMemberB = memberRepository.findById(memberB.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(8000);
		assertThat(findMemberB.getMoney()).isEqualTo(12000);
	}

	@Test
	@DisplayName("이체 중 예외 발생")
	public void accountTransferEx() throws SQLException {
		// given
		Member memberA = new Member(Member_A, 10000);
		Member memberEx = new Member(Member_EX, 10000);
		memberRepository.save(memberA);
		memberRepository.save(memberEx);

		// when
		assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
			.isInstanceOf(IllegalStateException.class);

		// then
		Member findMemberA = memberRepository.findById(memberA.getMemberId());
		Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(10000);
		assertThat(findMemberEx.getMoney()).isEqualTo(10000);
	}
}