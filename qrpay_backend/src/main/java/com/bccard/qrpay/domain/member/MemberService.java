package com.bccard.qrpay.domain.member;

import com.bccard.qrpay.config.security.CustomPasswordEncoder;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.exception.AuthException;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.IdValidator;
import com.bccard.qrpay.utils.MpmDateTimeUtils;
import com.bccard.qrpay.utils.PasswordValidator;
import com.bccard.qrpay.utils.security.HashCipher;
import java.util.List;
import java.util.Optional;
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
    private final CustomPasswordEncoder customPasswordEncoder;

    @Transactional(readOnly = true)
    public Member findByMemberId(String memberId) {
        return memberQueryRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Member> findBy(String memberId) {
        return memberQueryRepository.findById(memberId);
    }

    @Transactional(readOnly = true)
    public Member findByLoginId(String loginId) {
        return memberQueryRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean exist(String loginId) {
        return memberQueryRepository.findByLoginId(loginId).isPresent();
    }

    @Transactional
    public void passwordFail(String memberId) {
        Member member = findByMemberId(memberId);
        member.onPasswordFail();
    }

    @Transactional(noRollbackFor = {AuthException.class})
    public Member authenticate(String loginId, String password) {
        Member member = memberQueryRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new AuthException(QrpayErrorCode.MEMBER_NOT_FOUND));

        if (member.isAccountLock()) {
            throw new AuthException(QrpayErrorCode.ACCOUNT_LOCKED_POLICY);
        }

        if (!customPasswordEncoder.matches(password, member.getHashedPassword())) {
            member.onPasswordFail();
            throw new AuthException(QrpayErrorCode.INVALID_CREDENTIAL);
        }

        member.onLogin();

        return member;
    }

    public String hashPassword(String password) {
        return HashCipher.sha256EncodedBase64(password);
    }

    public String createNewMemberId() {
        Long seq = memberQueryRepository.getNextSequenceValue();
        return StringUtils.leftPad(seq.toString(), 8, 'T');
    }

    @Transactional(readOnly = true)
    public List<Member> findMemberByRole(Merchant merchant, MemberRole role) {
        List<Member> allMembers = memberQueryRepository.findAllMembers(merchant);

        return allMembers.stream()
                .filter(member -> member.getRole() == role)
                .filter(member -> member.getStatus() != MemberStatus.CANCELLED)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Member> findMembers(Merchant merchant) {
        return memberQueryRepository.findAllMembers(merchant).stream()
                .filter(member -> member.getStatus() != MemberStatus.CANCELLED)
                .sorted()
                .toList();
    }

    @Transactional
    public Member updatePermissionToCancel(Member member, boolean permissionToCancel) {
        Member m = memberQueryRepository
                .findById(member.getMemberId())
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));

        boolean nowPermission = m.getPermissionToCancel() != null && m.getPermissionToCancel();

        if (nowPermission != permissionToCancel) {
            m.updatePermissionToCancel(permissionToCancel);
        }
        return m;
    }

    @Transactional
    public Member updateStatus(Member member, MemberStatus status) {
        Member m = memberQueryRepository
                .findById(member.getMemberId())
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));

        if (m.getStatus() != MemberStatus.CANCELLED && m.getStatus() != status) {
            m.updateStatus(status);
        }
        return m;
    }

    @Transactional
    public Member cancelEmployee(Member requestor, String memberId) {
        Member toCancelMember = memberQueryRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));

        if (!requestor
                .getMerchant()
                .getMerchantId()
                .equals(toCancelMember.getMerchant().getMerchantId())) {
            throw new MemberException(QrpayErrorCode.MEMBER_CANCEL_REQUESTOR_INVALID_AUTHORIZATION);
        }

        if (toCancelMember.getRole() != MemberRole.EMPLOYEE) {
            throw new MemberException(QrpayErrorCode.MEMBER_CANCEL_NOT_EMPLOYEE);
        }

        if (toCancelMember.getStatus() != MemberStatus.CANCELLED) {
            toCancelMember.cancel();
        }

        return toCancelMember;
    }

    @Transactional
    public int cancelAll(Merchant merchant) {
        String canceledAt = MpmDateTimeUtils.generateDtmNow(MpmDateTimeUtils.PATTERN_YEAR_TO_DATE);
        return memberCUDRepository.updateStatusToCancelByMerchantId(merchant.getMerchantId(), canceledAt);
    }

    @Transactional
    public Member updatePassword(Member member, String newPassword) {
        Member m = memberQueryRepository
                .findById(member.getMemberId())
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));

        if (!PasswordValidator.isValid(newPassword)) {
            throw new MemberException(QrpayErrorCode.PASSWORD_POLICY_VIOLATION);
        }

        String toChangePassword = customPasswordEncoder.encode(newPassword);

        if (m.getHashedPassword().equals(toChangePassword)) {
            throw new MemberException(QrpayErrorCode.DISALLOW_CURRENT_PASSWORD_REUSE);
        }

        m.updatePassword(customPasswordEncoder.encode(newPassword));
        return m;
    }

    @Transactional
    public Member addEmployee(Merchant merchant, String employeeLoginId, String password, boolean permissionToCancel) {

        if (!IdValidator.isValid(employeeLoginId)) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_POLICY_VIOLATION);
        }

        if (exist(employeeLoginId)) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_CONFLICT);
        }

        if (!PasswordValidator.isValid(password)) {
            throw new MemberException(QrpayErrorCode.PASSWORD_POLICY_VIOLATION);
        }

        String newMemberId = createNewMemberId();
        Member newMem = Member.createNewEmployee(
                newMemberId, merchant, employeeLoginId, customPasswordEncoder.encode(password), permissionToCancel);
        return memberCUDRepository.save(newMem);
    }
}
