package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	LogRepository logRepository;

	/**
	 * memberService 	@Transactional:OFF
	 * memberRepository @Transactional:ON
	 * logRepository 	@Transactional:ON
	 */
	@Test
	public void outTxOff_success() {
	    // given
		String username = "outerTxOff_success";

	    // when
		memberService.joinV1(username);

	    // then : 모든 데이터가 정상 저장된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService 	@Transactional:OFF
	 * memberRepository @Transactional:ON
	 * logRepository 	@Transactional:ON  exception
	 */
	@Test
	public void outTxOff_fail() {
		// given
		String username = "로그예외_outerTxOff_success";

		// when
		assertThatThrownBy(() -> memberService.joinV1(username))
			.isInstanceOf(RuntimeException.class);

		// when:
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService 	@Transactional:ON
	 * memberRepository @Transactional:OFF
	 * logRepository 	@Transactional:OFF
	 */
	@Test
	void singleTx() {
		// given
		String username = "outerTxOff_success";

		// when
		memberService.joinV1(username);

		// then : 모든 데이터가 정상 저장된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}
}