package com.bccard.qrpay.domain.member;

import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.MemberErrorCode;
import com.bccard.qrpay.utils.security.HashCipher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberQueryRepository memberQueryRepository;
    private final MemberRepository memberCUDRepository;

    public Member findByMemberId(String memberId) {
        return memberQueryRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findMyLoginId(String loginId) {
        return memberQueryRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public boolean exist(String loginId) {
        return memberQueryRepository.findByLoginId(loginId).isPresent();
    }

    @Transactional
    public void passwordFail(String memberId) {
        Member member = findByMemberId(memberId);
        member.onPasswordFail();
    }

    public String hashPassword(String password) {
        return HashCipher.sha256EncodedBase64(password);
    }

    public String createNewMemberId() {
        Long seq = memberQueryRepository.getNextSequenceValue();
        String pad = StringUtils.leftPad(seq.toString(), 8, '0');
        return "";
    }
}
